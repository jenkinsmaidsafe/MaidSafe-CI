package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHUser;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Benjamin Bollen on 10/10/14.
 */

public class maidsafeRepository {
    private static final Logger logger = Logger.getLogger(maidsafeRepository.class.getName());
    private final String _repoID; // "developer/repository:MAID" as a unique ID
    private GHUser _developer;
    private ConcurrentMap<Integer, maidsafePullRequest> pulls;

    maidsafeRepository(String repoID) {
        this._repoID = repoID.trim();
        logger.log(Level.INFO, "Initialised maidsafeRepository for {0}", repoID);
    }

    public String getRepoID() {
        return this._repoID;
    }

    public void onPullRequest(GHEventPayload.PullRequest pr) throws IllegalStateException {
        // check whether ID matches
        String repoID = pr.getPullRequest().getHead().getRepository().getFullName()+':'+
                pr.getPullRequest().getHead().getRef();
        if (!_repoID.equals(repoID)) {
            logger.log(Level.SEVERE, "ERROR: pull request {0} passed to maidsafeRepository {1} doesn't match.",
                    new Object[]{repoID.trim(), _repoID});
            throw new IllegalStateException("ID constructed from PR " + repoID + " does not match " + _repoID);
        }

        if ("opened".equals(pr.getAction()) || "reopened".equals(pr.getAction())) {
            logger.log(Level.INFO, "Pull request (re)opened");

        }
    }

    public String getDevLogin() {
        if (_developer == null) {
            return new String("");
        } else {
            return _developer.getLogin();
        }
    }
}
