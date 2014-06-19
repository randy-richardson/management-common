package org.terracotta.management.resource;

public class ExceptionEntityV2 extends AbstractEntityV2{
  private String message;
  private String stackTrace;
  
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public String getStackTrace() {
    return stackTrace;
  }
  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }
  
  public String toJSON() {
    return String.format("{\"message\" : \"%s\", \"stackTrace\" : \"%s\"}", message, stackTrace);
  }
}
