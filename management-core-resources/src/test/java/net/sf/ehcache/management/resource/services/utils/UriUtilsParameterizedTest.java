/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
 */
package net.sf.ehcache.management.resource.services.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import static com.terracotta.management.resource.services.utils.UriUtils.MASK;
import static com.terracotta.management.resource.services.utils.UriUtils.maskQueryParams;
import static com.terracotta.management.resource.services.utils.UriUtils.removeQueryParams;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;
import static org.glassfish.jersey.uri.UriComponent.decodeQuery;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class UriUtilsParameterizedTest {
  @Parameterized.Parameters(name = "{index}: uri={0}")
  public static Collection<Object[]> getTestUris() {
    return Arrays.asList(new Object[][]{
        // no query string
        {
            URI.create("https://terracottatech.com/"),
            URI.create("https://terracottatech.com/"),
            URI.create("https://terracottatech.com/"),
        },
        // empty parameters
        {
            URI.create("https://terracottatech.com/?"),
            URI.create("https://terracottatech.com/?"),
            URI.create("https://terracottatech.com/?"),
        },
        // no username and password
        {
            URI.create("https://terracottatech.com/k1=v1&k1=v1&k2=v2"),
            URI.create("https://terracottatech.com/k1=v1&k1=v1&k2=v2"),
            URI.create("https://terracottatech.com/k1=v1&k1=v1&k2=v2"),
        },
        // only username
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=admin"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/"),
        },
        // relative path
        {
            URI.create("https://terracottatech.com/path/../?username=abc"),
            URI.create("https://terracottatech.com/path/../?username=" + MASK),
            URI.create("https://terracottatech.com/path/../?"),
        },
        // only password
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?password=se$cr$et$"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/"),
        },
        // empty username and password
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=&password="),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=" + MASK + "&password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/"),
        },
        // both username and password
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=admin&password=se$cr$et%24$"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?username=" + MASK + "&password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/"),
        },
        // other parameters
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&username=admin&password=se$cr$et$&k3=v3%24"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&k3=v3%24&username=" + MASK + "&password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&k3=v3%24&"),
        },
        // multiple entries
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&username=admin&password=se$cr$et$&k3=v3%24&username=admin1&password=secret"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&k3=v3%24&username=" + MASK + "&password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents/?k1=v1&k1=v2&k2=v3&k3=v3%24&"),
        },
        // matrix parameters
        {
            URI.create("https://terracottatech.com/tmc/api/v2/agents;ids=BigmemoryCluster/topologies?username=admin&password=%24Test12345%24%25%40%23%24"),
            URI.create("https://terracottatech.com/tmc/api/v2/agents;ids=BigmemoryCluster/topologies?username=" + MASK + "&password=" + MASK),
            URI.create("https://terracottatech.com/tmc/api/v2/agents;ids=BigmemoryCluster/topologies?"),
        }
    });
  }

  private final URI input;
  private final URI expectedMasked;
  private final URI expectedRemoved;

  public UriUtilsParameterizedTest(URI input, URI expectedMasked, URI expectedRemoved) {
    this.input = input;
    this.expectedMasked = expectedMasked;
    this.expectedRemoved = expectedRemoved;
  }

  @Test
  public void testMaskQueryParams() {
    assertThat(maskQueryParams(input), anyQueryOrder(expectedMasked));
  }

  @Test
  public void testRemoveQueryParams() {
    assertThat(removeQueryParams(input), anyQueryOrder(expectedRemoved));
  }

  public static Matcher<URI> anyQueryOrder(URI uri) {
    return new TypeSafeMatcher<URI>() {
      private final URI expected = uri;
      private URI expectedSorted;
      private URI actualSorted;

      @Override
      protected boolean matchesSafely(URI actual) {
        this.expectedSorted = sortQueryParams(uri);
        this.actualSorted = sortQueryParams(actual);
        return this.expectedSorted.equals(actualSorted);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("a URI equal to [expected: ").appendValue(expected)
            .appendText(", expectedSorted: ").appendValue(expectedSorted).appendText("]");
      }

      @Override
      protected void describeMismatchSafely(URI actual, Description mismatchDescription) {
        mismatchDescription.appendText("was [actual: ").appendValue(actual)
            .appendText(", actualSorted: ").appendValue(actualSorted).appendText("]");
      }
    };
  }

  private static URI sortQueryParams(URI uri) {
    String sortedQueryParams = decodeQuery(uri, false).entrySet().stream()
        .sorted(comparingByKey())
        .flatMap(entry -> entry.getValue().stream()
            .sorted()
            .map(value -> entry.getKey() + "=" + value))
        .collect(joining("&"));

    try {
      return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
          uri.getPort(), uri.getPath(), sortedQueryParams, uri.getFragment());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
