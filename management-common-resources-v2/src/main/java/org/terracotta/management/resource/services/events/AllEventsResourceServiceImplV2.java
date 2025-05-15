/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 */
package org.terracotta.management.resource.services.events;

import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.resource.events.EventEntityV2;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.security.Principal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.terracotta.management.resource.services.utils.UriUtils.maskQueryParams;

/**
 * A resource service for sending events.
 *
 * Since {@link TerracottaEventOutput} does not flushes events by itself, messages are flushed only once every
 * {@link #BATCH_SIZE} times. To prevent events lingering in the queue waiting for the {@link #BATCH_SIZE} quota to be
 * reached, a timer performs a flush every {@link #TIMER_INTERVAL} ms.
 * Finally, since Jersey does not close event outputs itself (even when the TCP connection was dropped,
 * see JERSEY-2833 for details), event outputs get closed if they've been idle for {@link #MAX_IDLE_KEEPALIVE} ms.
 * Note: this is NOT a bug in Jersey: event outputs have to be explicitly closed.
 *
 * This must be marked as @Singleton otherwise Jersey will create a new instance of this class per request,
 * creating as many timer threads.
 *
 * @author Ludovic Orban
 */
@Path("/v2/events")
@Singleton
public class AllEventsResourceServiceImplV2 {

  private static final Logger LOG = LoggerFactory.getLogger(AllEventsResourceServiceImplV2.class);

  public static final int BATCH_SIZE = Integer.getInteger("TerracottaEventOutput.batch_size", 32);
  public static final long TIMER_INTERVAL = Long.getLong("TerracottaEventOutput.timer_interval", 917L);

  // Making the reaper timer interval a non-round number of seconds to reduce the probability that a race condition
  // occurs causing multiple events to fire
  public static final long MAX_IDLE_KEEPALIVE = Long.getLong("TerracottaEventOutput.max_idle_keepalive", 57917L);

  private final EventServiceV2 eventService;

  // This broadcaster is only used for book keeping purposes.
  private final Broadcaster broadcaster;

  // TAB-6785 : it's as if @Singleton had no effects, so making sure here to instantiate only 1 timer
  private static final Timer flushTimer = new Timer("sse-flush-timer", true);

  public AllEventsResourceServiceImplV2() {
    this.eventService = ServiceLocator.locate(EventServiceV2.class);

    this.broadcaster = new Broadcaster();
    LOG.debug("sse-flush-timer being used: {}", flushTimer);
    flushTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        LOG.debug("There are {} registered SSE event output(s), checking them", broadcaster.outputs.size());
        for (Map.Entry<TerracottaEventOutput, TerracottaEventOutputFlushingMetadata> entry : broadcaster.outputs.entrySet()) {
          TerracottaEventOutput output = entry.getKey();
          TerracottaEventOutputFlushingMetadata metadata = entry.getValue();
          long idleTime = metadata.accumulatedIdleTime.addAndGet(TIMER_INTERVAL);

          int unflushedCount = metadata.unflushedCount.get();
          if (unflushedCount > 0) {
            LOG.debug("A SSE event output accumulated {} unflushed events during max interval, flushing it",
                      unflushedCount);
            try {
              output.flush();
            } catch (Exception e) {
              LOG.debug("Error flushing SSE from timer, closing event output", e);
              broadcaster.close(output);
            } finally {
              metadata.unflushedCount.addAndGet(-unflushedCount);
            }
          } else if (idleTime >= MAX_IDLE_KEEPALIVE) {
            LOG.debug("A SSE event output has been idle for too long {}, closing it", idleTime);
            broadcaster.close(output);
          } else {
            LOG.debug("A SSE event output accumulated 0 event during flush interval");
          }

        } // for
      }

    }, TIMER_INTERVAL, TIMER_INTERVAL);
  }

  @GET
  @Produces(SseFeature.SERVER_SENT_EVENTS)
  public TerracottaEventOutput getServerSentEvents(@Context UriInfo info, @QueryParam("localOnly") boolean localOnly,
                                                   @Context HttpServletRequest request,
                                                   @Context HttpServletResponse response) {
    Principal principal = request.getUserPrincipal();
    String userName = principal != null ? principal.getName() : "tc_no_security_ctxt";
    EventServiceListener eventOutput = new EventServiceListener(userName);

    LOG.debug("Invoking AllEventsResourceServiceImplV2.getServerSentEvents: info={}, localOnly={}, user={}",
        maskQueryParams(info.getRequestUri()), localOnly, userName);

    broadcaster.add(eventOutput);
    eventService.registerEventListener(eventOutput, localOnly);

    return eventOutput;
  }

  private class Broadcaster extends SseBroadcaster {

    private final Map<TerracottaEventOutput, TerracottaEventOutputFlushingMetadata> outputs = new ConcurrentHashMap<TerracottaEventOutput, TerracottaEventOutputFlushingMetadata>();

    @Override
    public void onException(final ChunkedOutput<OutboundEvent> chunkedOutput, final Exception exception) {
      LOG.debug("Error writing to OutputEvent", exception);
      close(chunkedOutput);
    }

    @Override
    public <OUT extends ChunkedOutput<OutboundEvent>> boolean add(OUT chunkedOutput) {
      outputs.put((TerracottaEventOutput) chunkedOutput, new TerracottaEventOutputFlushingMetadata());
      return super.add(chunkedOutput);
    }

    @Override
    public void onClose(final ChunkedOutput<OutboundEvent> chunkedOutput) {
      outputs.remove(chunkedOutput);
      eventService.unregisterEventListener((EventServiceListener) chunkedOutput);
    }

    public void close(final ChunkedOutput<OutboundEvent> chunkedOutput) {
      try {
        if (!chunkedOutput.isClosed()) {
          chunkedOutput.close();
        }
      } catch (Exception e) {
        LOG.debug("Error closing SSE event output from timer", e);
      } finally {
        onClose(chunkedOutput);
        remove(chunkedOutput);
      }
    }
  }

  public class EventServiceListener extends TerracottaEventOutput implements EventServiceV2.EventListener {
    private final String userName;

    public EventServiceListener(String userName) {
      super();
      this.userName = userName;
    }

    @Override
    public synchronized void write(OutboundEvent chunk) throws IOException {
      if (isClosed()) {
        throw new IOException("closed");
      }

      TerracottaEventOutputFlushingMetadata metadata = broadcaster.outputs.get(this);
      metadata.accumulatedIdleTime.set(0L);
      int unflushedCount = metadata.unflushedCount.incrementAndGet();

      try {
        super.write(chunk);
      } finally {
        if (unflushedCount == BATCH_SIZE) {
          LOG.debug("A SSE event output reached {} unflushed events, flushing it", unflushedCount);
          metadata.unflushedCount.addAndGet(-unflushedCount);
          super.flush();
        } else {
          LOG.debug("A SSE event output accumulating {} unflushed events", unflushedCount);
        }
      }
    }

    @Override
    public void onEvent(EventEntityV2 eventEntity) {
      OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
      eventBuilder.reconnectDelay(100);
      eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
      eventBuilder.name(EventEntityV2.class.getSimpleName());
      eventBuilder.data(EventEntityV2.class, eventEntity);
      OutboundEvent event = eventBuilder.build();

      try {
        write(event);
      } catch (Exception e) {
        onError(e);
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug(String.format("Event dispatched: {AgentId: %s, Type: %s, ApiVersion: %s, Representables: %s}",
            eventEntity.getAgentId(), eventEntity.getType(), eventEntity.getApiVersion(),
            eventEntity.getRootRepresentables()));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      LOG.debug("Error when waiting for management events.", throwable);
      try {
        broadcaster.close(this);
      } catch (Exception e) {
        LOG.debug("Error closing SSE event output", e);
      }
    }

    @Override
    public String getUsername() {
      return userName;
    }

    public String toString() {
      return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
  }

  private static class TerracottaEventOutputFlushingMetadata {
    final AtomicInteger unflushedCount = new AtomicInteger();
    final AtomicLong accumulatedIdleTime = new AtomicLong();
  }

}
