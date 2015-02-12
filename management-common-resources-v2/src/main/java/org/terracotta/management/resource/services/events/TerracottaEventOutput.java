package org.terracotta.management.resource.services.events;

import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class exists to overcome an defect seen in Chrome where the Jersey EventOutput uses "\n\n" as the
 * chunkDelimeter. The symptom would be a net::ERR_INVALID_CHUNKED_ENCODING on the Javascript EventSource receiving a
 * Server-Sent Event and that event would be malformed.
 * <p/>
 * Another problem this class solves is that it makes {@link #write} thread-safe: it should be but isn't in
 * ChunkedOutput. To keep performance intact despite thread-safety, messages are flushed only once every
 * {@link #BATCH_SIZE} times. To prevent events lingering in the queue waiting for a {@link #BATCH_SIZE} quorum,
 * a timer performs a flush every 100ms.
 *
 * @author gkeim
 * @author Ludovic Orban
 */
public class TerracottaEventOutput extends ChunkedOutput<OutboundEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(TerracottaEventOutput.class);

  public static final int     BATCH_SIZE     = Integer.getInteger("TerracottaEventOutput.batch_size", 32);
  public static final long    TIMER_INTERVAL = Long.getLong("TerracottaEventOutput.timer_interval", 200L);

  private final Timer         flushTimer;
  private final Field         flushingField;
  private int                 counter        = 0;

  public TerracottaEventOutput() {
    super("\n\n".getBytes(Charset.forName("UTF-8")));

    flushTimer = new Timer("sse-flush-timer", true);
    flushTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          if (!isClosed()) {
            flush();
          }
        } catch (Exception e) {
          LOG.warn("Error flushing SSE from timer", e);
        }
      }
    }, TIMER_INTERVAL, TIMER_INTERVAL);

    try {
      flushingField = ChunkedOutput.class.getDeclaredField("flushing");
      flushingField.setAccessible(true);
    } catch (NoSuchFieldException nsfe) {
      throw new RuntimeException(nsfe);
    }
  }

  private void switchOffAutoFlushing(boolean flushing) {
    try {
      flushingField.set(this, flushing);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException(iae);
    }
  }

  @Override
  public synchronized void write(OutboundEvent chunk) throws IOException {
    boolean flush = ++counter % BATCH_SIZE == 0;

    if (!flush) {
      switchOffAutoFlushing(true);
    }
    try {
      super.write(chunk);
    } finally {
      if (!flush) {
        switchOffAutoFlushing(false);
      }
    }
  }

  private synchronized void flush() throws IOException {
    super.write(null);
  }

  @Override
  public void close() throws IOException {
    flushTimer.cancel();
    super.close();
  }
}
