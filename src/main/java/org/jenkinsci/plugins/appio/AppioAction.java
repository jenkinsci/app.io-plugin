package org.jenkinsci.plugins.appio;

import hudson.model.Action;

/**
 * @author Kohsuke Kawaguchi
 */
public class AppioAction implements Action {
	
	private final String appURL;

    public AppioAction(String appURL) {
        this.appURL = appURL;
    }

    public String getIconFileName() {
        return "setting.png";
    }

    public String getDisplayName() {
        return "App.io iOS Simulator";
    }

    public String getUrlName() {
        return appURL;
    }
    
	public String getAppURL() {
		return appURL;
	}
}
