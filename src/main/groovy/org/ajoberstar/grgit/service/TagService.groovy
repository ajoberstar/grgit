/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithOperations
import org.ajoberstar.grgit.operation.TagAddOp
import org.ajoberstar.grgit.operation.TagListOp
import org.ajoberstar.grgit.operation.TagRemoveOp

/**
 * Provides support for performing tag-related operations on
 * a Git repository.
 *
 * <p>
 *   Details of each operation's properties and methods are available on the
 *   doc page for the class. The following operations are supported directly on
 *   this service instance.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.TagAddOp add}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.TagListOp list}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.TagRemoveOp remove}</li>
 * </ul>
 *
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 */
@WithOperations(instanceOperations=[TagListOp, TagAddOp, TagRemoveOp])
class TagService {
  private final Repository repository

  TagService(Repository repository) {
    this.repository = repository
  }
}
