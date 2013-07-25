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

import org.apache.commons.codec.binary.Base64;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;
import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Mark Prichard
 */
public class AppioServiceTest {

    // Test properties loaded from properties file
    private static final String propertyFile = "test.properties";

	// AppioService test variables
	private String apiKeyUnencoded = null;
	private String apiKey = null;
	private String appName = null;
	private String badKey = null;
	private String badName = null;

	// Amazon S3 test variables
	private String accessKey = null;
	private String secretKey = null;
	private String bucketName = null;
	private String keyName = null;
	private String uploadFile = null;

	private Properties testProperties = new Properties();

	// Set logging levels
	static {
		Logger l = Logger.getLogger(AppioService.class.getName());
		l.setLevel(Level.ALL);
		ConsoleHandler h = new ConsoleHandler();
		h.setLevel(Level.ALL);
		l.addHandler(h);
	}

	public AppioServiceTest() {
		super();
		loadTestProperties();
	}

	// Utility to load test properties
	public void loadTestProperties() {
		try {
            testProperties.load(new FileInputStream(propertyFile));

			apiKeyUnencoded = testProperties.getProperty("Appio.apiKeyUnencoded");
			byte[] encodedBytes = Base64.encodeBase64(apiKeyUnencoded.getBytes());
			apiKey = new String(encodedBytes);

			appName = testProperties.getProperty("Appio.appName");
			badKey = testProperties.getProperty("Appio.badKey");
			badName = testProperties.getProperty("Appio.badName");

			accessKey = testProperties.getProperty("S3.accessKey");
			secretKey = testProperties.getProperty("S3.secretKey");
			bucketName = testProperties.getProperty("S3.bucketName");
			keyName = testProperties.getProperty("S3.keyName");
			uploadFile = testProperties.getProperty("S3.uploadFile");
			
			System.out.println("Using test properties from: " + propertyFile);
			System.out.println("Appio.appName = " + appName);
			System.out.println("Appio.badKey = " + badKey);
			System.out.println("Appio.badName = " + badName);
			System.out.println("S3.accessKey = " + accessKey);
			System.out.println("S3.secretKey = " + secretKey);
			System.out.println("S3.bucketName = " + bucketName);
			System.out.println("S3.keyName = " + keyName);
			System.out.println("S3.uploadFile = " + uploadFile);
			
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void createApp() {
		AppioAppObject testAppObject = null;
		AppioService testService = new AppioService(apiKey);

		try {
			// Create a new Kickfolio app
			testAppObject = testService.createApp(appName);

			// Quick test for valid UUID
			UUID uid = UUID.fromString(testAppObject.getId());
			assertEquals(uid.toString().equals(testAppObject.getId()), true);

			// Cleanup: delete the app object
			Thread.sleep(5000);
			testService.deleteApp(testAppObject.getId());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void createAppBadKey() {
		AppioAppObject testAppObject = null;
		AppioService testService = new AppioService(badKey);

		try {
			testAppObject = testService.createApp(appName);

			// Quick test for valid UUID
			UUID uid = UUID.fromString(testAppObject.getId());
			assertEquals(uid.toString().equals(testAppObject.getId()), true);
		} catch (Exception e) {
			assertTrue("Exception expected: invalid App.io API key", true);
		}
	}

	@Test
	public void findApp() {
		AppioAppObject testAppObject = null;
		AppioService testService = new AppioService(apiKey);

		try {
			// Create a new Kickfolio app
			testAppObject = testService.createApp(appName);

			// Find the newly-created app
			testAppObject = testService.findApp(appName);

			// Quick test for valid UUID and appName
			UUID uid = UUID.fromString(testAppObject.getId());
			assertEquals(uid.toString().equals(testAppObject.getId()), true);
			assertEquals(testAppObject.getName(), appName);

			// Cleanup: delete the app
			Thread.sleep(1000);
			testService.deleteApp(testAppObject.getId());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void findAppNotFound() {
		AppioAppObject testAppObject = null;
		AppioService testService = new AppioService(apiKey);

		try {
			testAppObject = testService.findApp(badName);
			assertEquals(testAppObject.getId(), null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void findAppBadKey() {
		AppioAppObject testAppObject = null;
		AppioService testService = new AppioService(badKey);

		try {
			testAppObject = testService.findApp(appName);
			assertEquals(testAppObject.getId(), null);
		} catch (Exception e) {
			assertTrue("Exception expected: invalid App.io API key", true);
		}
	}

	@Test
	public void addVersionS3() {
		AppioService testService = new AppioService(apiKey);

		try {
			// Upload new bits via Amazon S3
			S3Service s3service = new S3Service(accessKey, secretKey);
			String fileUrl = s3service.getUploadUrl(bucketName, keyName, new File(uploadFile));

			// Create a new App.io app
			AppioAppObject testAppObject = testService.createApp(appName);

			// Add a new version and check for new version id
			AppioVersionObject testVersionObject = testService.addVersion(testAppObject.getId(), fileUrl);
			testAppObject = testService.findApp(appName);
			assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
			assertEquals(testAppObject.getVersion_ids()[0], testVersionObject.getId());

			// Repeat to test adding a second version
			testVersionObject = testService.addVersion(testAppObject.getId(), fileUrl);
			testAppObject = testService.findApp(appName);
			assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
			// Versions stored in reverse order: version_ids[0] has the latest
			assertEquals(testAppObject.getVersion_ids()[0], testVersionObject.getId());

			// Cleanup: delete the app object
			Thread.sleep(5000);
			testService.deleteApp(testAppObject.getId());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
