/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.terracotta.management.resource.exceptions.ExceptionUtils;

public class ExceptionEntityV2 extends AbstractEntityV2 {
  private String message;
  private String details;
  @JsonIgnore
  private Throwable throwable;

  public ExceptionEntityV2() {
  }

  public ExceptionEntityV2(Throwable throwable) {
    ErrorEntity errorEntity = ExceptionUtils.toErrorEntity(throwable);
    this.message = errorEntity.getError();
    this.details = errorEntity.getDetails();
    this.throwable = throwable;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public Throwable getThrowable() { return throwable; }

}
