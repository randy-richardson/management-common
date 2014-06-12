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
  private String sourceId;
  private String type;
  private String targetNodeId;
  private String targetJmxId;

  private final Map<String, Object> rootRepresentables = new HashMap<String, Object>();


  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTargetNodeId() {
    return targetNodeId;
  }

  public void setTargetNodeId(String targetNodeId) {
    this.targetNodeId = targetNodeId;
  }

  public String getTargetJmxId() {
    return targetJmxId;
  }

  public void setTargetJmxId(String targetJmxId) {
    this.targetJmxId = targetJmxId;
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
