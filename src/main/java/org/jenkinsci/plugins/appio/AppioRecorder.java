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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import org.apache.commons.codec.binary.Base64;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;
import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.framework.io.IOException2;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 * @author Mark Prichard
 */
public class AppioRecorder extends Recorder {
    private final String appFile;
    private final String appName;

    private File zip;

    public String getAppName() {
        return appName;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new AppioProjectAction(project);
    }

    @DataBoundConstructor
    public AppioRecorder(String appFile, String appName) {
        this.appFile = appFile;
        this.appName = appName;
    }

    public String getAppFile() {
        return appFile;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    
    public String resolve(String source, AbstractBuild build) {
        String resolved = Util.replaceMacro(source, build.getBuildVariableResolver());

        try {
            return build.getEnvironment().expand(resolved);
        } catch (Exception e) {
            return resolved;
        }
    }

    @SuppressWarnings("serial")
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws InterruptedException,
            IOException {

        if (build.getResult().isWorseOrEqualTo(Result.FAILURE))
            return false;

        String resolvedAppFile = resolve(appFile, build);
        final FilePath appPath = build.getWorkspace().child(resolvedAppFile);
        
        if (!appPath.exists()) {
            listener.getLogger().println("Build package: " + resolvedAppFile + " not exists");
            return false;
        }

        listener.getLogger().println("Deploying to App.io: " + appPath);

        List<AppioCredentials> credentialsList = CredentialsProvider.lookupCredentials(AppioCredentials.class, build.getProject());
        AppioCredentials appioCredentials = credentialsList.get(0);

        byte[] encodedBytes = Base64.encodeBase64(appioCredentials.getApiKey().getPlainText().getBytes());
        String appioApiKeyBase64 = new String(encodedBytes);

        listener.getLogger().println("Creating zipped package");

        try {
            // Zip <build>.app package for upload to S3
            try {
                zip = File.createTempFile("appio", "zip");
                appPath.zip(new FilePath(zip));
            } catch (IOException e) {
                throw new IOException2("Exception creating " + zip, e);
            }

            // Upload <build>.app.zip to S3 bucket
            String s3Url;
            try {
                S3Service s3service = new S3Service(appioCredentials.getS3AccessKey(), appioCredentials.getS3SecretKey().getPlainText());
                listener.getLogger().println("Uploading to S3 bucket: " + appioCredentials.getS3Bucket());

                s3Url = s3service.getUploadUrl(appioCredentials.getS3Bucket(), appName + build.getNumber(), zip);
                listener.getLogger().println("S3 Public URL: " + s3Url);
            } catch (Exception e) {
                throw new IOException2("Exception while uploading to S3" + zip, e);
            }

            // Create new app version on App.io
            try {
                // Check if app already exists on App.io
                AppioService appioService = new AppioService(appioApiKeyBase64);
                listener.getLogger().println("Checking for App.io app: " + appName);
                AppioAppObject appObject = appioService.findApp(appName);

                // App not found - user has ignored the validation error
                if (appObject.getId() == null) {
                    listener.getLogger().println("Cannot find application (" + appName + ") on App.io");
                    return false;
                }

                listener.getLogger().println("App.io application id: " + appObject.getId());

                // Add new version pointing to S3 URL
                listener.getLogger().println("Adding new version");
                AppioVersionObject versionObject = appioService.addVersion(appObject.getId(), s3Url);
                listener.getLogger().println("App.io version id: " + versionObject.getId());

                // Get the public App.io link for the app
                listener.getLogger().println("App.io URL: " + "https://app.io/" + appObject.getPublic_key());

                build.addAction(new AppioAction("https://app.io/" + appObject.getPublic_key()));
            } catch (Exception e) {
                throw new IOException2("Error uploading app/version to App.io", e);
            }

            return true;
        } finally {
            zip.delete();
        }
    }


    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        // Check that the application exists on App.io
        public FormValidation doCheckAppName(@QueryParameter("appName") String appName) {
            try {
                List<AppioCredentials> credentialsList = CredentialsProvider.lookupCredentials(AppioCredentials.class);
                AppioCredentials appioCredentials = credentialsList.get(0);

                byte[] encodedBytes = Base64.encodeBase64(appioCredentials.getApiKey().getPlainText().getBytes());
                String appioApiKeyBase64 = new String(encodedBytes);

                AppioService appioService = new AppioService(appioApiKeyBase64);
                AppioAppObject appObject = appioService.findApp(appName);

                if (appObject.getId() == null)
                    return FormValidation.error("App.io application not found: " + appName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return FormValidation.ok();
        }


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Upload to App.io";
        }
    }
}
