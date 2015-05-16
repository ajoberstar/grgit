/*
 * Copyright 2012-2015 the original author or authors.
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

package org.ajoberstar.grgit

import groovy.transform.Immutable
import org.eclipse.jgit.diff.DiffEntry

@Immutable
class CommitDiff {

    /**
     * The commit
     */
    Commit commit

    /**
     * Changes made in the commit
     */
    List<Diff> diffs

    /**
     * Represents changes made in a commit
     */
    static class Diff {
        /**
         * The name of the file that was changed
         */
        String fileName

        /**
         * The git object it
         */
        String objectId

        /**
         * The diff represented as a String
         */
        String diffAsString

        /**
         * The kind of change done to the file
         */
        DiffEntry.ChangeType changeType

        Diff(String fileName, String objectId, DiffEntry.ChangeType type) {
            this.fileName = fileName
            this.objectId = objectId
            this.changeType = type
        }

        Diff(String fileName, String objectId, DiffEntry.ChangeType type, String diffAsString) {
            this.fileName = fileName
            this.objectId = objectId
            this.changeType = type
            this.diffAsString = diffAsString
        }
    }
}