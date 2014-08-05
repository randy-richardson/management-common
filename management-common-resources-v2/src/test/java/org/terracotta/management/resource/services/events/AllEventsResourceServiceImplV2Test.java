package org.terracotta.management.resource.services.events;

import org.glassfish.jersey.media.sse.EventOutput;
import org.junit.Test;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.resource.events.EventEntityV2;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllEventsResourceServiceImplV2Test {

  @Test
  /**
   * This test verifies we call eventServiceV2.unregisterEventListener() when the channel closes
   */
  public void testGetServerSentEvents__unregisterAfterException() throws Exception {
    ServiceLocator locator = new ServiceLocator();
    MyEventServiceV2 eventServiceV2 = new MyEventServiceV2();
    locator.loadService(EventServiceV2.class, eventServiceV2);
    ServiceLocator.load(locator);

    AllEventsResourceServiceImplV2 allEventsResourceServiceImplV2 =  new AllEventsResourceServiceImplV2();
    UriInfo uriInfo =  mock(UriInfo.class);
    when(uriInfo.getRequestUri()).thenReturn(new URI(""));
    EventOutput eventOutput = allEventsResourceServiceImplV2.getServerSentEvents(uriInfo, false);
    EventServiceV2.EventListener listener = eventServiceV2.getListener();

    // this is the key thing here : we simulate browser closing the channel
    eventOutput.close();

    listener.onEvent(new EventEntityV2());
    assertTrue(eventServiceV2.isUnregisterEventListenerWasCalled());
  }


  class MyEventServiceV2 implements EventServiceV2{
    private EventListener eventListener;
    private boolean unregisterEventListenerWasCalled = false;

    @Override
    public void registerEventListener(EventListener listener, boolean localOnly) {
      eventListener = listener;
    }

    @Override
    public void unregisterEventListener(EventListener listener) {
      unregisterEventListenerWasCalled = true;
    }

    public EventListener getListener(){
      return this.eventListener;
    }

    public boolean isUnregisterEventListenerWasCalled() {
      return unregisterEventListenerWasCalled;
    }
}

}