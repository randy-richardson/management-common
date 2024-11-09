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

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;

import javax.servlet.Filter;
import java.util.Map;

/**
 * This class overrides the default Shiro authorization filters to return 403 (Forbidden) when access is denied.
 * The Shiro filters return 401 (Unauthorized) for some reason.
 *
 * Currently only the "roles" and "rest" filters are used, with "perms" being added for completeness’ sake.
 */
public class TCWebIniSecurityManagerFactory extends WebIniSecurityManagerFactory {
  @Override
  protected Map<String, ?> createDefaults(Ini ini, Ini.Section mainSection) {
    @SuppressWarnings("unchecked")
    Map<String, Filter> defaults = (Map<String, Filter>) super.createDefaults(ini, mainSection);
    defaults.replace("roles", new RolesFilter());
    defaults.replace("perms", new PermsFilter());
    defaults.replace("rest", new RestFilter());
    return defaults;
  }
}
