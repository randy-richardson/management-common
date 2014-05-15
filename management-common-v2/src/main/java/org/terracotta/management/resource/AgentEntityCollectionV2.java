package org.terracotta.management.resource;

import java.util.ArrayList;
import java.util.List;

public class AgentEntityCollectionV2 {

  private final List<AgentEntityV2> agentEntities = new ArrayList<AgentEntityV2>();

  private final List<ErrorEntity> errorEntities = new ArrayList<ErrorEntity>();

  public List<AgentEntityV2> getAgentEntities() {
    return agentEntities;
  }

  public List<ErrorEntity> getErrorEntities() {
    return errorEntities;
  }

}
