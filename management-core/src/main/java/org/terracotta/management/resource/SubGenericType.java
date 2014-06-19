package org.terracotta.management.resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.GenericType;

public class SubGenericType<T, S> extends GenericType<T> {
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

      @Override
      public String toString() {
        return "SubGenericType<" + type.getName() + ", " + subType.getName() + ">";
      }
    });
  }
}