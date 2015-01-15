/*
 * All content copyright (c) 2003-2012 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.management.l1bridge;

import java.io.Serializable;
import java.util.Set;

/**
 * The L1 bridge remote call descriptor contains everything that is necessary to describe a remote service call.
 *
 * @author Ludovic Orban
 */
public class RemoteCallDescriptor implements Serializable {

  private static final long serialVersionUID = 7481150306025461580L;
  private final String ticket;
  private final String token;
  private final String iaCallbackUrl;
  private final String serviceName;
  private final String methodName;
  private final Class[] paramClasses;
  private final Object[] params;
  private final Set<String> clientUUIDs;

  /**
   *
   * @param ticket
   * @param token
   * @param iaCallbackUrl
   * @param serviceName
   * @param methodName
   * @param paramClasses
   * @param params
   * @param clientUUIDs
   */
  public RemoteCallDescriptor(String ticket, String token, String iaCallbackUrl, String serviceName, String methodName, Class[] paramClasses, Object[] params,Set<String> clientUUIDs) {
    this.ticket = ticket;
    this.token = token;
    this.iaCallbackUrl = iaCallbackUrl;
    this.serviceName = serviceName;
    this.methodName = methodName;
    this.paramClasses = paramClasses;
    this.params = params;
    this.clientUUIDs = clientUUIDs;
  }

  public String getTicket() {
    return ticket;
  }

  public String getToken() {
    return token;
  }

  public String getIaCallbackUrl() {
    return iaCallbackUrl;
  }

  public String getServiceName() {
    return serviceName;
  }

  public String getMethodName() {
    return methodName;
  }

  public Class[] getParamClasses() {
    return paramClasses;
  }

  public Object[] getParams() {
    return params;
  }

  public Set<String> getClientUUIDs() {
    return clientUUIDs;
  }
}
