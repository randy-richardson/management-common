/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */

package org.terracotta.management.resource.services;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.resource.AgentEntityCollectionV2;
import org.terracotta.management.resource.AgentMetadataEntityV2;

import java.util.Collection;
import java.util.Set;

/**
 * @author Ludovic Orban
 */
public interface AgentServiceV2 {

  /**
   * A locator interface for this service.
   */
  interface Locator {
    AgentServiceV2 locateAgentService();
  }


  /**
   * Get a collection of agent entities known by this agent.
   * @param ids a set of IDs. If empty, this means all known agents.
   * @return
   * @throws ServiceExecutionException
   */
  AgentEntityCollectionV2 getAgents(Set<String> ids) throws ServiceExecutionException;


  Collection<AgentMetadataEntityV2> getAgentsMetadata(Set<String> ids) throws ServiceExecutionException;
}
