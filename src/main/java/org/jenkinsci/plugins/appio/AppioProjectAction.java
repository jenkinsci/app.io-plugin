package org.jenkinsci.plugins.appio;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;

/**
 * @author Kohsuke Kawaguchi
 */
public class AppioProjectAction implements ProminentProjectAction {
    private final AbstractProject<?, ?> project;

    public AppioProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    /**
     * Picks up the appio URL from the last known good build.
     */
   	private AppioAction getAction() {
           AbstractBuild<?,?> b = project.getLastBuild();
           for (int i=0; b!=null && i<10; i++) {
               AppioAction a = b.getAction(AppioAction.class);
               if (a!=null)    return a;
               b = b.getPreviousBuild();
           }
           return null;
   	}

    public String getIconFileName() {
        return "setting.png";
    }

    public String getDisplayName() {
        return "App.io Simulator Link";
    }

    public String getUrlName() {
        AppioAction a = getAction();
        return a!=null ? a.getUrlName() : null;
    }

    public String getAppURL() {
        AppioAction a = getAction();
        return a!=null ? a.getAppURL() : null;
   	}

}
