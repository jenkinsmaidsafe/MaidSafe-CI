package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Benjamin Bollen on 10/10/14.
 *
 * A maidsafeTask bundles pull requests over different submodules/repositories
 * into a single traceable issue.
 */


public class maidsafeTask {
    private static final Logger logger = Logger.getLogger(maidsafeTask.class.getName());
    private String _label;
    private ConcurrentMap<String, maidsafeRepository> Repositories;

    maidsafeTask(String label){
        this._label = label;
    }

    public String getLabel(){
        return _label;
    }

    public void onPullRequest(GHEventPayload.PullRequest pr) {

        logger.log(Level.INFO, "Pull request received for maidsafeTask {0}.", _label);

        maidsafeRepository msRepo = getRepository(pr.getPullRequest());

        try {
            msRepo.onPullRequest(pr);

            if ("opened".equals(pr.getAction()) || "reopened".equals(pr.getAction())) {
                logger.log(Level.INFO, "Pull request (re)opened");

            }

        } catch (IllegalStateException ex) {
            // TODO: catch
        }
    }

    private maidsafeRepository getRepository(GHPullRequest pullRequest) {
        maidsafeRepository ret;
        if (Repositories == null) {
            Repositories = new ConcurrentHashMap<String, maidsafeRepository>();
        }

        String repoID = pullRequest.getHead().getRepository().getFullName()+':'+
                pullRequest.getHead().getRef();

        logger.log(Level.INFO, "Pull request for repoID {0}", repoID);

        if (Repositories.containsKey(repoID)) {
            ret = Repositories.get(repoID);
            if (ret == null) {
                logger.log(Level.SEVERE, "Found existing maidsafeRepository for {0}, but is NULL", repoID);
                Repositories.replace(repoID, new maidsafeRepository(pullRequest));
                ret = Repositories.get(repoID);
            }
            logger.log(Level.INFO, "Found existing maidsafeRepository for {0}", ret.getRepoID());
        } else {
            Repositories.putIfAbsent(repoID, new maidsafeRepository(pullRequest));
            ret = Repositories.get(repoID);
            logger.log(Level.INFO, "Created new maidsafeRepository {0}", ret.getRepoID());
        }
        return ret;
    }
}
