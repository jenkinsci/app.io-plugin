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

package org.jenkinsci.plugins.appio.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.appio.model.AppioApp;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioApps;
import org.jenkinsci.plugins.appio.model.AppioVersion;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author markprichard
 *
 */
public class AppioService implements Serializable {

	private static final long serialVersionUID = 1L;
	private final HttpHost httpHost = new HttpHost("app.io", 443, "https");
	private final HttpPost httpPost = new HttpPost("/api/apps");
	private final HttpPost httpPostVersions = new HttpPost("/api/versions");
	private final HttpGet httpGet = new HttpGet("/api/apps");

	private final String appio_v1 = "application/vnd.app.io+json;version=1";

	private String apiKey = null;

	public AppioService(String apiKey) {
		super();
		this.apiKey = apiKey;
	}

    /**
	 * @param appName
	 * @return AppioAppObject (org.jenkinsci.plugins.appio.model.AppioAppObject)
	 * @throws Exception
	 */
	public AppioAppObject createApp(String appName) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioAppObject theAppObject = new AppioAppObject();

		try {
			// App.io Authorization and Content-Type headers
			String appioAuth = "Basic " + apiKey;
			httpPost.addHeader("Authorization", appioAuth);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", appio_v1);

			// Create App.io App object
			AppioAppObject appioAppObj = new AppioAppObject();
			appioAppObj.setName(appName);

			// We want to exclude all non-annotated fields
			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();

			// Construct {"app": ... } message body
			AppioApp theApp = new AppioApp();
			theApp.setApp(appioAppObj);
			StringEntity postBody = new StringEntity(gson.toJson(theApp),
					ContentType.create("application/json", "UTF-8"));
			httpPost.setEntity(postBody);
            LOGGER.fine("AppioService.createApp() Request: " + gson.toJson(theApp));

            // Call App.io REST API to create the new app
			HttpResponse response = httpClient.execute(httpHost, httpPost);
			String jsonAppioApp = handler.handleResponse(response);
            LOGGER.fine("AppioService.createApp() Response: " + jsonAppioApp);

            // Get JSON data from the HTTP Response
			AppioApp appioApp = new Gson().fromJson(jsonAppioApp,
					AppioApp.class);
			theAppObject = appioApp.getApp();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return theAppObject;
	}

	/**
	 * @param appId
	 */
	public void deleteApp(String appId) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete("/api/apps" + "/" + appId);

		// App.io Authorization and Content-Type headers
		String appioAuth = "Basic " + apiKey;
		httpDelete.addHeader("Authorization", appioAuth);
		httpDelete.addHeader("Accept", appio_v1);

		try {
            LOGGER.fine("AppioService.deleteApp(): deleting app id " + appId);
            httpClient.execute(httpHost, httpDelete);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
	}
	
	/**
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public AppioAppObject findApp(String appName) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioAppObject theApp = new AppioAppObject();

		try {
			// App.io Authorization and Content-Type headers
			String appioAuth = "Basic " + apiKey;
			httpGet.addHeader("Authorization", appioAuth);
			httpGet.addHeader("Accept", appio_v1);

            LOGGER.fine("AppioService.findApp() Request");
            HttpResponse response = httpClient.execute(httpHost, httpGet);
			String jsonAppioApps = handler.handleResponse(response);
            LOGGER.fine("AppioService.findApp() Response: " + jsonAppioApps);

            AppioApps appioApps = new Gson().fromJson(jsonAppioApps,
					AppioApps.class);
			List<AppioAppObject> list = Arrays.asList(appioApps.getApps());
			Iterator<AppioAppObject> iterator = list.iterator();

			boolean foundAppName = false;
			while ((iterator.hasNext()) && (!foundAppName)) {
				AppioAppObject thisApp = iterator.next();

				if (thisApp.getName().equals(appName)) {
                    theApp = thisApp;
                    foundAppName = true;
				}
			}

        } catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return theApp;
	}

	/**
	 * @param appId
	 * @param urlUpload
	 * @return
	 * @throws Exception
	 */
	public AppioVersionObject addVersion(String appId, String urlUpload)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioVersion newVersion = new AppioVersion();
		AppioVersionObject versionObj = new AppioVersionObject();

		try {
			// Construct {"version": ... } message body
			versionObj.setApp_id(appId);
			versionObj.setBundle_url(urlUpload);
			newVersion.setVersion(versionObj);
            LOGGER.fine("AppioService.addVersion() Request: "
                        + new Gson().toJson(newVersion));

            // Send new version REST call to Appio
			httpPostVersions.addHeader("Authorization", "Basic " + apiKey);
			httpPostVersions.addHeader("Content-Type", "application/json");
			httpPostVersions.addHeader("Accept", appio_v1);
			StringEntity reqBody = new StringEntity(
					new Gson().toJson(newVersion), ContentType.create(
							"application/json", "UTF-8"));
			httpPostVersions.setEntity(reqBody);
			HttpResponse response = httpClient.execute(httpHost,
					httpPostVersions);

			String jsonAppioVersion = handler.handleResponse(response);
            LOGGER.fine("AppioService.addVersion() Response: " + jsonAppioVersion);
            newVersion = new Gson().fromJson(jsonAppioVersion,
					AppioVersion.class);

		} catch (Exception e) {
			e.getStackTrace();
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return newVersion.getVersion();
	}

    private static final Logger LOGGER = Logger.getLogger(AppioService.class.getName());
}
