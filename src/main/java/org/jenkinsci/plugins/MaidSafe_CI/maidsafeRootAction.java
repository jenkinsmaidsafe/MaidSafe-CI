package org.jenkinsci.plugins.MaidSafe_CI;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.UnprotectedRootAction;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.github.GitHub;

/**
 * Created by Benjamin Bollen on 07/10/14.
 * Copyright by MaidSafe ltd
 */


@Extension
public class maidsafeRootAction implements UnprotectedRootAction {

    static final String URL = "maidsafehook"; // configure webhook for pull requests
    private static final Logger logger = Logger.getLogger(maidsafeRootAction.class.getName());
    private maidsafeGitHub msgh;
    private ConcurrentMap<String, maidsafeTask> msTasks;


    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "maidsafeRootAction";
    }

    public String getUrlName() {
        return URL;
    }

    public void doIndex(StaplerRequest req, StaplerResponse resp) {

        //TODO: introduce a maidsafeBouncer that guards entry into the system,
        //      originated from official maidsafe repository;
        //      whitelisted authors should be checked before build execution
        //      until that stage new code should be quarantined

        logger.log(Level.INFO, "Triggered doIndex");
        String event = req.getHeader("X-GitHub-Event");
        String payload = req.getParameter("payload"); // setup GitHub webhook
        if (payload == null) {
            logger.log(Level.SEVERE, "Request does not contain a payload.");
            return;
        }

        if (msgh  == null) {
            msgh = new maidsafeGitHub();
        }

        logger.log(Level.INFO, "Received payload event: {0}", event);
        try {
            if ("pull_request".equals(event)) {
                GHEventPayload.PullRequest pr;
                pr = msgh.get().parseEventPayload(new StringReader(payload), GHEventPayload.PullRequest.class);
                String label = pr.getPullRequest().getHead().getLabel();
                logger.log(Level.INFO, "Label of pull request: {0}", label);
                logger.log(Level.INFO, "Author account of PR {0}.", pr.getPullRequest().getHead().getUser().getLogin());
                logger.log(Level.INFO, "Reference branch of PR {0}.", pr.getPullRequest().getHead().getRef());
                logger.log(Level.INFO, "Pull request number {0}.", pr.getNumber());
                logger.log(Level.INFO, "SHA of PR {0}.", pr.getPullRequest().getHead().getSha());
                logger.log(Level.INFO, "URL of PR {0}.", pr.getPullRequest().getHead().getRepository().getFullName());

                maidsafeTask labelledTask = getTask(label);
                labelledTask.onPullRequest(pr);

            } else if ("issue_comment".equals(event)) {
                logger.log(Level.INFO, "Comment issued, not handled yet in code.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to parse GitHub hook payload.", ex);
        }
        logger.log(Level.INFO, "Finished handling event.");
    }

    private maidsafeTask getTask(String label) {
        // look for existing maidsafeTask, or create new Task to track label

        logger.log(Level.INFO, "Requesting maidsafeTask for label {0}", label);
        if (msTasks == null) {
            logger.log(Level.INFO, "List of tasks is null; initialising new map.");
            msTasks = new ConcurrentHashMap<String, maidsafeTask>();
        }

        String trimLabel = label.trim();
        maidsafeTask ret;
        if (msTasks.containsKey(trimLabel)) {
            ret = msTasks.get(trimLabel);
            logger.log(Level.INFO, "Found existing maidsafeTask for {0}", trimLabel);
        } else {
            ret = new maidsafeTask(trimLabel);
            logger.log(Level.INFO, "Created new maidsafeTask for label {0}", trimLabel);
        }

        return ret;
    }

    /*private Set<maidsafeTask> getTasks(GHRepository repo) throws IOException {
        try {
            return getRepos(repo.getOwner().getLogin() + "/" + repo.getName());
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
            return getRepos(owner + "/" + repo.getName());
        }
    }

    private Set<maidsafeTask> getRepos(String repo) {
        final Set<maidsafeTask> ret = new HashSet<maidsafeTask>();

        // We need this to get access to list of repositories
        Authentication old = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(ACL.SYSTEM);

        try {
            for (AbstractProject<?, ?> job : Jenkins.getInstance().getAllItems(AbstractProject.class)) {
                GhprbTrigger trigger = job.getTrigger(GhprbTrigger.class);
                if (trigger == null || trigger.getRepository() == null) {
                    continue;
                }
                GhprbRepository r = trigger.getRepository();
                if (repo.equals(r.getName())) {
                    ret.add(r);
                }
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(old);
        }
        return ret;
    }*/



}
