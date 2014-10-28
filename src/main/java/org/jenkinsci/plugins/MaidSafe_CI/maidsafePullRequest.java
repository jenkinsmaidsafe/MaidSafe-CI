package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Benjamin Bollen on 10/10/14.
 */


public class maidsafePullRequest {
    private static final Logger logger = Logger.getLogger(maidsafePullRequest.class.getName());
    private triBool MergeableState;
    private final int id;
    private maidsafeRepository repo;
    private Vector<String> SHAs;  //
    //private ConcurrentMap<String, > anything history related, mapped on hash

    maidsafePullRequest(GHEventPayload.PullRequest pr, maidsafeRepository repo) {
        id = pr.getNumber();
        SHAs = new Vector<String>();
        SHAs.add(pr.getPullRequest().getHead().getSha());
        setMergeableState(pr.getPullRequest());
        this.repo = repo;
        logger.log(Level.INFO, "MaidSafe Pull Request number {1} initialised with SHA {0}.", new Object[]{SHAs.lastElement(), id});
    }

    public int getNumber() throws IllegalStateException {
        return id;
    }

    public String getLastSHA(){
        if (SHAs.isEmpty()){
            throw new IllegalStateException("No SHA given in PR " + id + " of repository " + repo.getRepoID());
        }

        return SHAs.lastElement();
    }

    private void setMergeableState(GHPullRequest pr) {
        boolean isMergeable;
        try {
           isMergeable = pr.getMergeable();
           MergeableState = isMergeable ? triBool.TRUE: triBool.FALSE;
        } catch (IOException ex) {
            MergeableState = triBool.UNKNOWN;
        }
    }

    public void synchronise(GHPullRequest pr) {

    }
}
