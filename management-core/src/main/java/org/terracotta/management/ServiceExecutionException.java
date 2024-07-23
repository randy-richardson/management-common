/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */

package org.terracotta.management;

/**
 * A generic service exception class for management service components that are leveraged by application actions.
 * Exceptions should include a message that could be presented to a user.
 *
 * @author brandony
 */
public class ServiceExecutionException extends Exception {
  public ServiceExecutionException() {
    super();
  }

  public ServiceExecutionException(String message) {
    super(message);
  }

  public ServiceExecutionException(String message,
                                   Throwable cause) {
    super(message, cause);
  }

  public ServiceExecutionException(Throwable cause) {
    super(cause);
  }
}
