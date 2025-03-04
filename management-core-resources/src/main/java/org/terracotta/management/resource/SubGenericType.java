/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package org.terracotta.management.resource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jakarta.ws.rs.core.GenericType;

/**
 * @author Ludovic Orban
 */
public class SubGenericType<T, S> extends GenericType<T> {
  private final Class<T> type;
  private final Class<S> subType;

  public SubGenericType(final Class<T> type, final Class<S> subType) {
    super(new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
        return new Type[] { subType };
      }

      @Override
      public Type getRawType() {
        return type;
      }

      @Override
      public Type getOwnerType() {
        return type;
      }
    });
    this.type = type;
    this.subType = subType;
  }

  @Override
  public boolean equals(Object obj) {
    if (SubGenericType.class.equals(obj.getClass())) {
      SubGenericType other = (SubGenericType)obj;
      return other.type.equals(type) && other.subType.equals(subType);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.type.hashCode() + this.subType.hashCode();
  }

  @Override
  public String toString() {
    return "SubGenericType<" + type.getName() + ", " + subType.getName() + ">";
  }

}