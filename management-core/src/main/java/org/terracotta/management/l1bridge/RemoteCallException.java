/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.management.l1bridge;

/**
 * @author Ludovic Orban
 */
public class RemoteCallException extends Exception {

  public RemoteCallException(String message) {
    super(message);
  }

  public RemoteCallException(String message, Throwable cause) {
    super(message, cause);
  }
}
