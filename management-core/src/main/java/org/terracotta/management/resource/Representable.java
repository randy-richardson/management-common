/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */

package org.terracotta.management.resource;

import java.io.Serializable;

/**
 * <p>
 * An interface that identifies a resource representation served from embedded management web services.
 * </p>
 * 
 * @author brandony
 * 
 */
public interface Representable extends Serializable {
  String EMBEDDED_AGENT_ID = "embedded";

  /**
   * <p>
   * Get the identifier for the agent that provided this representable object.
   * </p>
   * 
   * @return
   */
  String getAgentId();

  /**
   * <p>
   * Set the identifier for the agent that provided this representable object.
   * </p>
   * 
   * @param agentId
   */
  void setAgentId(String agentId);
}
