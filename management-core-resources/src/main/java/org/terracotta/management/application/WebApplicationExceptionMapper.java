/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Ludovic Orban
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  @Override
  public Response toResponse(WebApplicationException exception) {
    LOG.debug("WebApplicationExceptionMapper caught exception", exception);
    return Response.status(exception.getResponse().getStatus())
        .type((String)exception.getResponse().getMetadata().getFirst("Content-Type"))
        .entity(exception.getResponse().getEntity()).build();
  }

}
