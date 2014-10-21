package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHPullRequest;

import java.util.Vector;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Benjamin Bollen on 10/10/14.
 */


public class maidsafePullRequest {
    private static final Logger logger = Logger.getLogger(maidsafePullRequest.class.getName());
    private boolean isMergeable;
    private final int id;
    private maidsafeRepository repo
;    private Vector<String> SHAs;  //
    //private ConcurrentMap<String, > anything history related, mapped on hash

    maidsafePullRequest(GHPullRequest pr, maidsafeRepository repo) {
        id = pr.getNumber();
        SHAs = new Vector<String>();
        SHAs.add(pr.getHead().getSha());
        this.repo = repo;
        logger.log(Level.INFO, "MaidSafe Pull Request number {1} initialised with SHA {0}.", new Object[]{SHAs.lastElement(), id});
    }

    public int getNumber() {
        return id;
    }

    public void synchronise(GHPullRequest pr) {

    }
}
