/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package org.terracotta.management.resource.events;

import java.util.HashMap;
import java.util.Map;

import org.terracotta.management.resource.VersionedEntityV2;

/**
 * A {@link org.terracotta.management.resource.VersionedEntityV2} representing a topology event
 * from the management API.
 *
 * @author Ludovic Orban
 */
public class EventEntityV2 extends VersionedEntityV2 {

  private String agentId;
  private String type;

  private final Map<String, Object> rootRepresentables = new HashMap<String, Object>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getAgentId() {
    return agentId;
  }

  @Override
  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public Map<String, Object> getRootRepresentables() {
    return rootRepresentables;
  }
}
