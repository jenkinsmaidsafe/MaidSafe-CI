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

        if ("opened".equals(pr.getAction()) || "reopened".equals(pr.getAction())) {
            logger.log(Level.INFO, "Pull request (re)opened");

        }
    }

    private maidsafeRepository getRepository(GHPullRequest pullRequest) {
        final GHRepository repo = pullRequest.getRepository();
        try {
            logger.log(Level.INFO, "Pull request full name {0}",
                     pullRequest.getHead().getRepository().getFullName());
            logger.log(Level.INFO, "DEBUG: catch clause {0}",
                     repo.getUrl());
            return getRepository(pullRequest.getHead().getRepository().getFullName());
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Can't get a valid owner for repo " + repo.getName());
            // this normally happens due to missing "login" field in the owner of the repo
            // when the repo is inside of an organisation account. The only field which doesn't
            // rely on the owner.login (which would throw a null pointer exception) is the "html_url"
            // field. So we try to parse the owner out of that here until github fixes his api
            String repoUrl = repo.getUrl();
            if (repoUrl.endsWith("/")) {// strip off trailing slash if any
                repoUrl = repoUrl.substring(0, repoUrl.length() - 2);
            }
            int slashIndex = repoUrl.lastIndexOf('/');
            String owner = repoUrl.substring(slashIndex + 1);
            logger.log(Level.INFO, "Parsed {0} from {1}", new Object[]{owner, repoUrl});
            return getRepository(owner + "/" + repo.getName());
        }
    }

    private maidsafeRepository getRepository(String repo) {

        if (Repositories == null) {
            Repositories = new ConcurrentHashMap<String, maidsafeRepository>();
        }

        String trimRepo = repo.trim();
        maidsafeRepository ret;

        if (Repositories.containsKey(trimRepo)) {
            ret = Repositories.get(trimRepo);
            logger.log(Level.INFO, "Found existing Repository for {0}", trimRepo);
        } else {
            ret = new maidsafeRepository(trimRepo);
            logger.log(Level.INFO, "Created new maidsafeRepository {0}", trimRepo);
        }

        return ret;
    }


}
