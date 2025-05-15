/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 */

package org.terracotta.management.security.web.shiro;

import java.util.Arrays;
import java.util.Collection;

public interface TCRealmSupport {
  String TERRACOTTA_PERM = "api:read";

  String OPERATOR_PERM = "api:read";

  Collection<String> ADMIN_PERMS = Arrays.asList("api:update", "api:create", "api:delete");
}