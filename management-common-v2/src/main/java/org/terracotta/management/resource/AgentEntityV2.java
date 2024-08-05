/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.resource;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * Represents an agent for some monitorable application. It provides access to the root {@link Representable} objects 
 * in an appropriate state as determined by the resource providing it.
 * </p>
 * 
 * @author brandony
 * 
 */
public class AgentEntityV2 extends AbstractEntityV2 {
  private String agencyOf;
  private final Map<String, Object> rootRepresentables = new HashMap<String, Object>();

  public String getAgencyOf() {
    return agencyOf;
  }

  public void setAgencyOf(String agencyOf) {
    this.agencyOf = agencyOf;
  }

  public Map<String, Object> getRootRepresentables() {
    return rootRepresentables;
  }

}
