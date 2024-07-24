/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */

package org.terracotta.management.resource.services;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.resource.AgentEntityV2;
import org.terracotta.management.resource.AgentMetadataEntityV2;
import org.terracotta.management.resource.ResponseEntityV2;
import java.util.Set;

/**
 * @author Ludovic Orban
 */
public interface AgentServiceV2 {

  /**
   * Get a collection of agent entities known by this agent.
   * @param ids a set of IDs. If empty, this means all known agents.
   * @return a ResponseEntityV2
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<AgentEntityV2> getAgents(Set<String> ids) throws ServiceExecutionException;

  /**
   * Get a collection of agent metadata entities known by this agent.
   * @param ids a set of IDs. If empty, this means all known agents.
   * @return a ResponseEntityV2
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<AgentMetadataEntityV2> getAgentsMetadata(Set<String> ids) throws ServiceExecutionException;

}
