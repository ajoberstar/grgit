package org.ajoberstar.grgit.operation
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.eclipse.jgit.api.DescribeCommand
import org.eclipse.jgit.api.errors.GitAPIException

import java.util.concurrent.Callable

/**
 * Find the nearest tag reachable from HEAD. Returns an {@link String}}.
 *
 * <p>Find the most recent tag that is reachable from HEAD.  If the tag points to the commit, then only the tag is
 * shown. Otherwise, it suffixes the tag name with the number of additional commits on top of the tagged object and the
 * abbreviated object name of the most recent commit.</p>
 *
 * <pre>
 * def description = grgit.description()
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>.
 *
 * @see <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>
 */
class DescribeOp implements Callable<String> {
    private final Repository repo
    private final Object commit

    DescribeOp(Repository repo){
        this.repo = repo
        this.commit = null
    }

    String call(){
        DescribeCommand cmd = repo.jgit.describe()
        try {
            return cmd.call()
        } catch (GitAPIException e) {
            throw new GrgitException('Problem retrieving description.', e)
        }

    }
}

