/*
 * The MIT License
 *
 * Copyright (c) 2013 Mark Prichard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.appio;

import hudson.model.ProminentProjectAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

/**
 * @author Kohsuke Kawaguchi
 * @author Mark Prichard
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
		AbstractBuild<?, ?> b = project.getLastBuild();
		for (int i = 0; b != null && i < 10; i++) {
			AppioAction a = b.getAction(AppioAction.class);
			if (a != null)
				return a;
            else {
                b = b.getPreviousBuild();
            }
		}
        return new AppioAction("https://app.io/prismadrop");
	}

	public String getIconFileName() {
		return "setting.png";
	}

	public String getDisplayName() {
		return "App.io Simulator Link";
	}

	public String getUrlName() {
		AppioAction a = getAction();
		return a != null ? a.getUrlName() : null;
	}

	public String getAppURL() {
		AppioAction a = getAction();
		return a != null ? a.getAppURL() : null;
	}

}
