/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.resource.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.terracotta.management.resource.ErrorEntity;

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

  /**
   * Convert a throwable to an ErrorEntity.
   * 
   * @param t
   *          the throwable.
   * @return the ErrorEntity describing the throwable.
   */
  public static ErrorEntity toErrorEntity(Throwable t) {
    String errorMessage = "";
    if (t != null) {
      String message = t.getMessage();
      errorMessage = message == null ? "" : message.replace('\"', '\'');
    }

    String extraErrorMessage = "";
    Throwable rootCause = getRootCause(t);
    if (rootCause != t && rootCause != null && rootCause.getMessage() != null) {
      extraErrorMessage = rootCause.getMessage().replace('\"', '\'');
    }

    ErrorEntity errorEntity = new ErrorEntity();
    errorEntity.setError(errorMessage);
    errorEntity.setDetails(extraErrorMessage);
    return errorEntity;
  }

}
