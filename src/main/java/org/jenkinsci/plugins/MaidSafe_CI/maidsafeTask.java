package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;

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

    maidsafeTask(String label){
        this._label = label;
    }
    public String getLabel(){
        return _label;
    }

    public void onPullRequest(GHEventPayload.PullRequest pr) {

        logger.log(Level.INFO, "Pull request received for maidsafeTask {0}.", _label);
        if ("opened".equals(pr.getAction()) || "reopened".equals(pr.getAction())) {
            logger.log(Level.INFO, "Pull request opened");

        }
    }
}
