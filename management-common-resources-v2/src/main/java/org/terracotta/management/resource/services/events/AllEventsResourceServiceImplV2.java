/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * A resource service for sending events.
 * 
 * @author Ludovic Orban
 */
@Path("/v2/events")
public class AllEventsResourceServiceImplV2 {

  private static final Logger LOG = LoggerFactory.getLogger(AllEventsResourceServiceImplV2.class);

  private final EventServiceV2 eventService;
  private final SseBroadcaster broadcaster;

  public AllEventsResourceServiceImplV2() {
    this.eventService = ServiceLocator.locate(EventServiceV2.class);
    this.broadcaster = new Broadcaster();
  }

  private class Broadcaster extends SseBroadcaster {
    @Override
    public void onException(final ChunkedOutput<OutboundEvent> chunkedOutput, final Exception exception) {
      LOG.debug("Error writing to OutputEvent", exception);
    }

    @Override
    public void onClose(final ChunkedOutput<OutboundEvent> chunkedOutput) {
      eventService.unregisterEventListener((EventServiceListener) chunkedOutput);
    }
  }

  public class EventServiceListener extends TerracottaEventOutput implements EventServiceV2.EventListener {
    @Override
    public void onEvent(EventEntityV2 eventEntity) {
      OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
      eventBuilder.reconnectDelay(100);
      eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
      eventBuilder.name(EventEntityV2.class.getSimpleName());
      eventBuilder.data(EventEntityV2.class, eventEntity);
      OutboundEvent event = eventBuilder.build();

      AllEventsResourceServiceImplV2.this.broadcaster.broadcast(event);

      if (LOG.isDebugEnabled()) {
        LOG.debug(String.format("Event dispatched: {AgentId: %s, Type: %s, ApiVersion: %s, Representables: %s}",
                                eventEntity.getAgentId(), eventEntity.getType(), eventEntity.getApiVersion(),
                                eventEntity.getRootRepresentables()));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      LOG.debug("Error when waiting for management events.", throwable);
    }
  }

  @GET
  @Produces(SseFeature.SERVER_SENT_EVENTS)
  public TerracottaEventOutput getServerSentEvents(@Context UriInfo info, @QueryParam("localOnly") boolean localOnly) {
    LOG.debug(String.format("Invoking AllEventsResourceServiceImplV2.getServerSentEvents: %s", info.getRequestUri()));

    EventServiceListener eventOutput = new EventServiceListener();

    broadcaster.add(eventOutput);
    eventService.registerEventListener(eventOutput, localOnly);

    return eventOutput;
  }
}
