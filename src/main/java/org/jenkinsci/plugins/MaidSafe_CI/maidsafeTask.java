package org.jenkinsci.plugins.MaidSafe_CI;

/**
 * Created by Benjamin Bollen on 10/10/14.
 *
 * A maidsafeTask bundles pull requests over different submodules/repositories
 * into a single traceable issue.
 */


public class maidsafeTask {
    private String _label;

    maidsafeTask(String label){
        this._label = label;
    }
    public String getLabel(){
        return _label;
    }
}
