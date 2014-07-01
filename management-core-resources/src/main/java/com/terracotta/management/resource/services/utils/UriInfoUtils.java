/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package com.terracotta.management.resource.services.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

/**
 * @author Ludovic Orban
 */
public class UriInfoUtils {

  static Set<String> PRODUCTS = new HashSet<String>() {
                                {
                                  add("TMS");
                                  add("WAN");
                                  add("USER");
                                }
                              };

  public static Set<String> extractProductIds(UriInfo info) {
    List<String> ids = info.getQueryParameters().get("productIds");
    if (ids == null) {
      return null;
    }

    Set<String> result = new HashSet<String>();
    for (String idsString : ids) {
      List<String> idNames = Arrays.asList(idsString.split(","));
      for (String idName : idNames) {
        if (idName.equals("*")) {
          result.addAll(PRODUCTS);
        }
        try {
          result.add(idName);
        } catch (IllegalArgumentException iae) {
          // ignore
        }
      }
    }
    return result;
  }

  public static Set<String> extractAgentIds(UriInfo info) {
    String value = info.getPathSegments().get(0).getMatrixParameters().getFirst("ids");

    Set<String> values;
    if (value == null) {
      values = Collections.emptySet();
    } else {
      values = new HashSet<String>(Arrays.asList(value.split(",")));
    }

    return values;
  }

  public static String extractLastSegmentMatrixParameter(UriInfo info, String parameterName) {
    return info.getPathSegments().get(info.getPathSegments().size() - 1).getMatrixParameters().getFirst(parameterName);
  }

  public static Set<String> extractLastSegmentMatrixParameterAsSet(UriInfo info, String parameterName) {
    String value = extractLastSegmentMatrixParameter(info, parameterName);
    return value == null ? null : new HashSet<String>(Arrays.asList(value.split(",")));
  }

}
