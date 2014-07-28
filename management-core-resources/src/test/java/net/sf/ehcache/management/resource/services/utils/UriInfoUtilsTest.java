/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package net.sf.ehcache.management.resource.services.utils;

import org.junit.Test;

import com.terracotta.management.resource.services.utils.UriInfoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ludovic Orban
 */
public class UriInfoUtilsTest {

  @Test
  public void testExtractLastSegmentMatrixParameterAsSetAllowsBothMultivalueAndCsv() throws Exception {
    UriInfo uriInfo = mock(UriInfo.class);
    PathSegment pathSegment1 = mock(PathSegment.class);
    PathSegment lastPathSegment = mock(PathSegment.class);

    List<PathSegment> pathSegments = new ArrayList<PathSegment>();
    pathSegments.add(pathSegment1);
    pathSegments.add(lastPathSegment);

    when(uriInfo.getPathSegments()).thenReturn(pathSegments);
    when(lastPathSegment.getMatrixParameters()).thenReturn(new MultivaluedHashMap<String, String>() {{
      put("param1", Arrays.asList("value11,value12", "value21,value22"));
      put("param2", Arrays.asList("valueAA,valueAB,valueBA,valueBB"));
      put("param3", Arrays.asList("valueAA", "valueAB", "valueBA", "valueBB"));
    }});

    Set<String> params = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(uriInfo, "param1");
    assertThat(params.size(), is(4));
    assertThat(params.containsAll(Arrays.asList("value11", "value12", "value21", "value22")), is(true));

    params = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(uriInfo, "param2");
    assertThat(params.size(), is(4));
    assertThat(params.containsAll(Arrays.asList("valueAA", "valueAB", "valueBA", "valueBB")), is(true));

    params = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(uriInfo, "param3");
    assertThat(params.size(), is(4));
    assertThat(params.containsAll(Arrays.asList("valueAA", "valueAB", "valueBA", "valueBB")), is(true));
  }

  @Test
  public void testExtractLastSegmentMatrixParameterAsSetNoParam() throws Exception {
    UriInfo uriInfo = mock(UriInfo.class);
    PathSegment pathSegment1 = mock(PathSegment.class);
    PathSegment lastPathSegment = mock(PathSegment.class);

    List<PathSegment> pathSegments = new ArrayList<PathSegment>();
    pathSegments.add(pathSegment1);
    pathSegments.add(lastPathSegment);

    when(uriInfo.getPathSegments()).thenReturn(pathSegments);
    when(lastPathSegment.getMatrixParameters()).thenReturn(new MultivaluedHashMap<String, String>());


    Set<String> params = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(uriInfo, "param1");
    assertThat(params, is(nullValue()));
  }
}
