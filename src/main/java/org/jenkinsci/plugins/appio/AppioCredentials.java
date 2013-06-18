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

import hudson.Extension;
import hudson.util.Secret;

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.BaseCredentials;

/**
 * @author Kohsuke Kawaguchi
 * @author Mark Prichard
 */
public class AppioCredentials extends BaseCredentials {

    private static final long serialVersionUID = 1L;

    // App.io API Key
    private final Secret apiKey;
    
    // AWS Credentials and S3 Bucket
    private final String s3AccessKey;
    private final Secret s3SecretKey;
    private final String s3Bucket;

    @DataBoundConstructor
    public AppioCredentials(String s3AccessKey, Secret s3SecretKey, String s3Bucket, Secret apiKey) {
        this.s3AccessKey = s3AccessKey;
        this.s3SecretKey = s3SecretKey;
        this.s3Bucket = s3Bucket;
        this.apiKey = apiKey;
    }

    public Secret getApiKey() {
		return apiKey;
	}

	public String getS3AccessKey() {
		return s3AccessKey;
	}

	public Secret getS3SecretKey() {
		return s3SecretKey;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	@Extension
    public static class DescriptorImpl extends CredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "App.io Credentials";
        }
    }
}
