/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.management.application;

import java.util.HashSet;
import java.util.Set;

import org.terracotta.management.resource.services.AgentsResourceServiceImplV2;

/**
 * A class that adds the commonly used
 * resources and providers.
 * @author Ludovic Orban
 */
public class DefaultApplicationV2 {

  /**
   * Get a default set of resource and provider classes.
   * @return a default set of classes.
   */
  public Set<Class<?>> getClasses() {
    return new HashSet<Class<?>>() {{
        add(DefaultExceptionMapperV2.class);
        add(ResourceRuntimeExceptionMapperV2.class);
        add(WebApplicationExceptionMapperV2.class);
        add(AgentsResourceServiceImplV2.class);
    }};
  }

}
