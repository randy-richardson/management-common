/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */

package org.terracotta.management.resource.services;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brandony
 */
public class UtilsTest {
  @Test
  public void testTrimToNull() {
    String nullStr = null;
    Assert.assertNull(Utils.trimToNull(nullStr));

    String emptyStr = "";
    Assert.assertNull(Utils.trimToNull(emptyStr));

    String nonNullStr = "stuff";
    Assert.assertNotNull(Utils.trimToNull(nonNullStr));
    Assert.assertEquals(nonNullStr, Utils.trimToNull(nonNullStr));
  }
}
