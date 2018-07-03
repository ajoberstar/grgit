package org.ajoberstar.grgit;

import org.eclipse.jgit.api.errors.TransportException;

public class PushException extends TransportException {
  public PushException(String message) {
    super(message);
  }

  public PushException(String message, Throwable cause) {
    super(message, cause);
  }
}
