package org.ajoberstar.grgit.util

import java.nio.file.Path

final class CoercionUtil {
  private CoercionUtil() {
    throw new AssertionError('Can not instantiate this class.')
  }

  static File toFile(Object obj) {
    if (obj instanceof File) {
      return obj
    } else if (obj instanceof Path) {
      return obj.toFile()
    } else {
      return new File(obj.toString())
    }
  }
}
