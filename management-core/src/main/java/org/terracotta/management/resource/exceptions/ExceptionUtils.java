/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.management.resource.exceptions;

import java.lang.reflect.InvocationTargetException;

/**
 * Misc. utility methods that work on exceptions.
 *
 * @author Ludovic Orban
 */
public class ExceptionUtils {

  /**
   * Get the root cause of the specified throwable.
   * @param t the throwable.
   * @return the root cause.
   */
  public static Throwable getRootCause(Throwable t) {
    Throwable last = null;
    while (t != null && t != last) {
      last = t;
      t = t.getCause();
    }
    if (last instanceof InvocationTargetException) {
      last = ((InvocationTargetException)last).getTargetException();
    }
    return last;
  }

}
