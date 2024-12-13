/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
 */
package org.terracotta.management.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.resource.exceptions.ExceptionUtils;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Ludovic Orban
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionMapper.class);

  @Override
  public Response toResponse(Throwable exception) {
    LOG.debug("DefaultExceptionMapper caught exception", exception);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ExceptionUtils.toErrorEntity(exception)).build();
  }

}
