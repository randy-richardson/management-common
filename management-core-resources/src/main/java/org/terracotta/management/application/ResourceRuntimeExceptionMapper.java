/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 * Copyright IBM Corp. 2024, 2025
 */
package org.terracotta.management.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.resource.exceptions.ExceptionUtils;
import org.terracotta.management.resource.exceptions.ResourceRuntimeException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Ludovic Orban
 */
@Provider
public class ResourceRuntimeExceptionMapper implements ExceptionMapper<ResourceRuntimeException> {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceRuntimeExceptionMapper.class);

  @Override
  public Response toResponse(ResourceRuntimeException exception) {
    LOG.debug("ResourceRuntimeExceptionMapper caught exception", exception);
    return Response.status(exception.getStatusCode())
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ExceptionUtils.toErrorEntity(exception)).build();
  }

}
