package org.ajoberstar.grgit

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includeNames=true)
class DiffEntry {
  /** General type of change a single file-level patch describes. */
  enum ChangeType {
    ADD,
    MODIFY,
    DELETE,
    RENAME,
    COPY;
  }

  /** General type of change indicated by the patch. */
  ChangeType changeType;

  /**
   * Get the old name associated with this file.
   * <p>
   * The meaning of the old name can differ depending on the semantic meaning
   * of this patch:
   * <ul>
   * <li><i>file add</i>: always <code>/dev/null</code></li>
   * <li><i>file modify</i>: always {@link #newPath}</li>
   * <li><i>file delete</i>: always the file being deleted</li>
   * <li><i>file copy</i>: source file the copy originates from</li>
   * <li><i>file rename</i>: source file the rename originates from</li>
   * </ul>
   *
   */
  String oldPath;

  /**
   * Get the new name associated with this file.
   * <p>
   * The meaning of the new name can differ depending on the semantic meaning
   * of this patch:
   * <ul>
   * <li><i>file add</i>: always the file being created</li>
   * <li><i>file modify</i>: always {@link #oldPath}</li>
   * <li><i>file delete</i>: always <code>/dev/null</code></li>
   * <li><i>file copy</i>: destination file the copy ends up at</li>
   * <li><i>file rename</i>: destination file the rename ends up at</li>
   * </ul>
   *
   */
  String newPath;

}
