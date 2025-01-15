/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
 */

package org.terracotta.management.resource;

import java.io.Serializable;

/**
 * An error representation.
 * @author Ludovic Orban
 */
public class ErrorEntity implements Serializable {
  private String error;
  private String details;

  public ErrorEntity() {
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String toJSON() {
    return String.format("{\"error\" : \"%s\" , \"details\" : \"%s\"}", error, details);
  }

}
