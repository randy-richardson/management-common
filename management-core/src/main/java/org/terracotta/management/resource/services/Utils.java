/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.resource.services;

/**
 * <p>Utility class for management resource services.</p>
 *
 * @author brandony
 */
public class Utils {

  /**
   * <p>A convenience method to prevent adding some massive dependency like commons lang.</p>
   *
   * @param string to trim
   * @return trimmed string or {@code null}
   */
  public static String trimToNull(String string) {
    return string == null || string.trim().length() == 0 ? null : string.trim();
  }
}
