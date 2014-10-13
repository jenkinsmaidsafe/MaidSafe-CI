package org.jenkinsci.plugins.MaidSafe_CI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

/**
 * Created by Benjamin Bollen on 08/10/14.
 */
public class maidsafeGitHub {
    private static final Logger logger = Logger.getLogger(maidsafeGitHub.class.getName());
    private GitHub gh;

    public GitHub get() throws IOException {
        if (gh == null) {
            connect();
        }
        return gh;
    }

    private void connect() throws IOException {
        final maidsafePrivateAccount privateAccount;
        String accessToken;
        final String serverAPIUrl = "https://api.github.com";

        // temporary hack; implement interface for Jenkins
        privateAccount = new maidsafePrivateAccount();
        accessToken = privateAccount.getPrivateAccessToken();

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                gh = GitHub.connectUsingOAuth(serverAPIUrl, accessToken);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Can't connect to GitHub.com using OAuth token.");
                throw ex;
            }
        } else {
            logger.log(Level.SEVERE, "Can't connect to GitHub.com without an OAuth token.");
            throw new IOException("No OAuth token specified.");
        }
    }

    public boolean isUserMemberOfMaidSafe(GHUser member) {
        boolean msHasMember = false;
        try {
            GHOrganization msOrg = get().getOrganization("maidsafe");
            msHasMember = msOrg.hasMember(member);
            logger.log(Level.FINE, "MaidSafe has member {0}: {2}",
                    new Object[]{member.getLogin(), msHasMember ? "yes" : "no"});
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not retrieve MaidSafe organisation object.");
            return false;
        }
        return msHasMember;
    }
}
