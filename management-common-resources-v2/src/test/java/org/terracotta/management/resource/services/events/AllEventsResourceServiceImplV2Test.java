package org.terracotta.management.resource.services.events;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.resource.events.EventEntityV2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

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
    TerracottaEventOutput eventOutput = allEventsResourceServiceImplV2.getServerSentEvents(uriInfo, false);
    EventServiceV2.EventListener listener = eventServiceV2.getListener();

    // this is the key thing here : we simulate browser closing the channel
    eventOutput.close();

    listener.onEvent(new EventEntityV2());
    assertTrue(eventServiceV2.isUnregisterEventListenerWasCalled());
  }

  @Test
  public void testGetServerSentEvents_loggingOnEvents() throws Exception {

    final List<LoggingEvent> logEventsList = new ArrayList<LoggingEvent>();
    Appender appender = new AppenderSkeleton() {
      @Override
      protected void append(LoggingEvent loggingEvent) {
        logEventsList.add(loggingEvent);
      }

      @Override
      public void close() {

      }

      @Override
      public boolean requiresLayout() {
        return false;
      }
    };

    Logger logger = Logger.getLogger(AllEventsResourceServiceImplV2.class);
    logger.addAppender(appender);
    logger.setLevel(Level.DEBUG);

    ServiceLocator.unload();
    ServiceLocator locator = new ServiceLocator();
    MyEventServiceV2 eventServiceV2 = new MyEventServiceV2();
    locator.loadService(EventServiceV2.class, eventServiceV2);
    ServiceLocator.load(locator);

    AllEventsResourceServiceImplV2 allEventsResourceServiceImplV2 =  new AllEventsResourceServiceImplV2();
    UriInfo uriInfo =  mock(UriInfo.class);
    when(uriInfo.getRequestUri()).thenReturn(new URI("mockURI"));
    TerracottaEventOutput eventOutput = allEventsResourceServiceImplV2.getServerSentEvents(uriInfo, false);
    EventServiceV2.EventListener listener = eventServiceV2.getListener();

    String mockVersion = "1.0";
    String mockType = "mockEvent";
    String mockAgentId = "mockAgent";
    EventEntityV2 mockEvent = new EventEntityV2();
    mockEvent.setApiVersion(mockVersion);
    mockEvent.setType(mockType);
    mockEvent.setAgentId(mockAgentId);
    listener.onEvent(mockEvent);

    String event = logEventsList.get(logEventsList.size() - 1).getMessage().toString();
    assertTrue(event.contains("Event dispatched"));
    assertTrue(event.contains(mockVersion));
    assertTrue(event.contains(mockType));
    assertTrue(event.contains(mockAgentId));
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
