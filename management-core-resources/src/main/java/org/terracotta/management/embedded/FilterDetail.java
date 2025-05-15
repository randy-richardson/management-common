/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */

package org.terracotta.management.embedded;

import jakarta.servlet.Filter;

/**
 * @author brandony
 */
public final class FilterDetail {
  public static final String[] SECURITY_DISPATCHERS = new String[]{"REQUEST", "FORWARD", "INCLUDE", "ERROR"};

  private final String pathSpec;

  private final String[] dispatcherNames;

  private final Filter filter;

  public FilterDetail(Filter filter,
                      String pathSpec) {
    this(filter, pathSpec, SECURITY_DISPATCHERS);
  }

  public FilterDetail(Filter filter,
                      String pathSpec,
                      String... dispatchNames) {
    this.filter = filter;
    this.pathSpec = pathSpec;
    this.dispatcherNames = dispatchNames;
  }

  public String getPathSpec() {
    return pathSpec;
  }

  public String[] getDispatcherNames() {
    return dispatcherNames;
  }

  public Filter getFilter() {
    return filter;
  }
}
