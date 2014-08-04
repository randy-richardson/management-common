package org.terracotta.management.resource;

import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ludovic Orban
 */
public class SubGenericTypeTest {

  @Test
  public void testEquals_differentObjects() throws Exception {
    SubGenericType<Collection, String> type1 = new SubGenericType<Collection, String>(Collection.class, String.class);
    SubGenericType<Collection, Integer> type2 = new SubGenericType<Collection, Integer>(Collection.class, Integer.class);

    assertThat(type1.equals(type2), is(false));
  }

  @Test
  public void testEquals_equalObjects() throws Exception {
    SubGenericType<Collection, String> type1 = new SubGenericType<Collection, String>(Collection.class, String.class);
    SubGenericType<Collection, String> type2 = new SubGenericType<Collection, String>(Collection.class, String.class);

    assertThat(type1.equals(type2), is(true));
  }

  @Test
  public void testEquals_sameObjects() throws Exception {
    SubGenericType<Collection, String> type1 = new SubGenericType<Collection, String>(Collection.class, String.class);

    assertThat(type1.equals(type1), is(true));
  }

  @Test
  public void testHashcode_equalObjects() throws Exception {
    SubGenericType<Collection, String> type1 = new SubGenericType<Collection, String>(Collection.class, String.class);
    SubGenericType<Collection, String> type2 = new SubGenericType<Collection, String>(Collection.class, String.class);

    assertThat(type1.hashCode() == type2.hashCode(), is(true));
  }

  @Test
  public void testHashcode_differentObjects() throws Exception {
    SubGenericType<Collection, String> type1 = new SubGenericType<Collection, String>(Collection.class, String.class);
    SubGenericType<Collection, Integer> type2 = new SubGenericType<Collection, Integer>(Collection.class, Integer.class);

    assertThat(type1.hashCode() == type2.hashCode(), is(false));
  }
}
