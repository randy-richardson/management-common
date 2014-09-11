package org.terracotta.management.resource.services.events;

import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.server.ChunkedOutput;

/**
 * This class exists to overcome an defect seen in Chrome where the Jersey EventOutput uses "\n\n" as the
 * chunkDelimeter. The symptom would be a net::ERR_INVALID_CHUNKED_ENCODING on the Javascript EventSource receiving a
 * Server-Sent Event and that event would be malformed.
 * 
 * @author gkeim
 */
public class TerracottaEventOutput extends ChunkedOutput<OutboundEvent> {
  public TerracottaEventOutput() {
    super("\r\n".getBytes());
  }
}
