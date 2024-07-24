/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.resource.exceptions.ExceptionUtils;
import org.terracotta.management.resource.exceptions.ResourceRuntimeException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
