/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 */
package com.terracotta.management.resource.services.utils;

import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.glassfish.jersey.uri.UriComponent.decodeQuery;

public class UriUtils {

  private static final Set<String> DEFAULT_QUERY_PARAMS_TO_SANITIZE = new HashSet<>();
  public static final String MASK = "XXXXX";

  static {
    DEFAULT_QUERY_PARAMS_TO_SANITIZE.add("username");
    DEFAULT_QUERY_PARAMS_TO_SANITIZE.add("password");
  }

  public static URI sanitizeQueryParams(URI uri, boolean shouldMask, Set<String> queryParams) {
    if (uri == null) {
      return null;
    }
    Set<String> all = decodeQuery(uri, false).keySet();
    Set<String> toSanitize = queryParams.stream()
        .filter(all::contains)
        .collect(Collectors.toSet());
    UriBuilder uriBuilder = UriBuilder.fromUri(uri);
    for (String queryParam : toSanitize) {
      if (shouldMask) {
        uriBuilder.replaceQueryParam(queryParam, MASK);
      } else {
        uriBuilder.replaceQueryParam(queryParam);
      }
    }
    return uriBuilder.build();
  }

  public static URI removeQueryParams(URI uri, Set<String> queryParams) {
    return sanitizeQueryParams(uri, false, queryParams);
  }

  /**
   * Removes a default set of sensitive query parameters (e.g., username, password).
   *
   * @param uri the URI whose query parameters should be removed
   * @return a new URI with the default query parameters removed, or {@code null} if the {@code uri} is null
   */
  public static URI removeQueryParams(URI uri) {
    return removeQueryParams(uri, DEFAULT_QUERY_PARAMS_TO_SANITIZE);
  }

  public static URI maskQueryParams(URI uri, Set<String> queryParams) {
    return sanitizeQueryParams(uri, true, queryParams);
  }

  /**
   * Masks a default set of sensitive query parameters (e.g. username, password).
   *
   * @param uri the URI whose query parameters should be masked
   * @return a new URI with the default query parameters masked, or {@code null} if the {@code uri} is null
   */
  public static URI maskQueryParams(URI uri) {
    return maskQueryParams(uri, DEFAULT_QUERY_PARAMS_TO_SANITIZE);
  }
}

