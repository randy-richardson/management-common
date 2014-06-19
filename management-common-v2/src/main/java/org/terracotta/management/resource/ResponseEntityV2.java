package org.terracotta.management.resource;

import java.util.ArrayList;
import java.util.Collection;

public class ResponseEntityV2 extends AbstractEntityV2{

  private final Collection<AbstractEntityV2> entities = new ArrayList<AbstractEntityV2>();
  private final Collection<ExceptionEntityV2> exceptionEntities = new ArrayList<ExceptionEntityV2>();

  public Collection<AbstractEntityV2> getEntities() {
    return entities;
  }

  public Collection<ExceptionEntityV2> getExceptionEntities() {
    return exceptionEntities;
  }

  /**
   * @return version detail for associated with this entity
   */
  public String getApiVersion() {
    return VERSION_V2;
  }


}
