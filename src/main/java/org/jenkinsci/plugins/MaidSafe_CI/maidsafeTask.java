package org.jenkinsci.plugins.MaidSafe_CI;

import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;

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

        maidsafeRepository msRepo = getRepository(pr.getPullRequest().getRepository());

        if ("opened".equals(pr.getAction()) || "reopened".equals(pr.getAction())) {
            logger.log(Level.INFO, "Pull request (re)opened");

        }
    }

    private maidsafeRepository getRepository(GHRepository repo) {
        try {
            logger.log(Level.INFO, "Repository from {0} named {1}.", new Object[]{repo.getOwner().getLogin(), repo.getName()});
            return getRepository(repo.getOwner().getLogin() + "/" + repo.getName());
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
        return null;
    }


}
