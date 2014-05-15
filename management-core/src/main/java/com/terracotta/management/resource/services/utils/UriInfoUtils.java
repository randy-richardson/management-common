/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package com.terracotta.management.resource.services.utils;

import java.util.Arrays;
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


}
