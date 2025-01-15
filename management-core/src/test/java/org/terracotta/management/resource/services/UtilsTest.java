/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
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
