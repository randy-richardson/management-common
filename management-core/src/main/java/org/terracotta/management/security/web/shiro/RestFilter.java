/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.management.security.web.shiro;

import java.io.IOException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter;
import org.apache.shiro.web.util.WebUtils;

public class RestFilter extends HttpMethodPermissionFilter {
  private static final String MESSAGE = "Access denied.";

  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
    if (!response.isCommitted()) {
      try {
        WebUtils.toHttp(response).sendError(403, MESSAGE);
      } catch (ClassCastException cce) {
        return super.onAccessDenied(request, response);
      }
    }

    return false;
  }
}
