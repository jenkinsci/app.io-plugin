package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;
import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.FilepickerService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

public class AppioServiceTest {

	// Test properties loaded via getClassLoader().getResourceAsStream()
	private String propertyPackage = ("org/jenkinsci/plugins/appio/");
	private String propertyFile = propertyPackage + "test.properties";

	// AppioService test variables
	private String apiKeyUnencoded = null;
	private String apiKey = null;
	private String appName = null;
	private String badKey = null;
	private String badName = null;
	
	// FilepickerService test variables
	private String fpFilePath = null;
	private String fpApiKey = null;

	// Amazon S3 test variables
	private String accessKey = null;
	private String secretKey = null;
	private String bucketName = null;
	private String keyName = null;
	private String uploadFile = null;

	private Properties testProperties = new Properties();

	public AppioServiceTest() {
		super();
		loadTestProperties();
	}

	// Utility to load test properties
	public void loadTestProperties() {
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(propertyFile);

		try {
			testProperties.load(in);

			apiKeyUnencoded = testProperties
					.getProperty("Appio.apiKeyUnencoded");
			byte[] encodedBytes = Base64.encodeBase64(apiKeyUnencoded
					.getBytes());
			apiKey = new String(encodedBytes);

			appName = testProperties.getProperty("Appio.appName");
			badKey = testProperties.getProperty("Appio.badKey");
			badName = testProperties.getProperty("Appio.badName");
			
			fpFilePath = testProperties.getProperty("Filepicker.filePath");
			fpApiKey = testProperties.getProperty("Filepicker.apiKey");

			accessKey = testProperties.getProperty("S3.accessKey");
			secretKey = testProperties.getProperty("S3.secretKey");
			bucketName = testProperties.getProperty("S3.bucketName");
			keyName = testProperties.getProperty("S3.keyName");
			uploadFile = testProperties.getProperty("S3.uploadFile");
			
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
	public void addVersionFilepicker() {
		AppioService testService = new AppioService(apiKey);

		try {
			// Upload new bits via Filepicker
			FilepickerService filepicker = new FilepickerService(fpApiKey);
			String fileUrl = filepicker.getUploadURL(fpFilePath);

			// Create a new App.io app
			AppioAppObject testAppObject = testService.createApp(appName);

			// Add a new version
			AppioVersionObject testVersionObject = testService.addVersion(
					testAppObject.getId(), fileUrl);

			// Get app info and check for new version id
			testAppObject = testService.findApp(appName);
			assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
			assertEquals(testAppObject.getVersion_ids()[0],
					testVersionObject.getId());

			// Cleanup: delete the app object
			Thread.sleep(5000);
			testService.deleteApp(testAppObject.getId());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void addVersionS3() {
		AppioService testService = new AppioService(apiKey);

		try {
			// Upload new bits via Amazon S3
			S3Service s3service = new S3Service(accessKey, secretKey);
			String fileUrl = s3service.getUploadUrl(bucketName, keyName,
					new File(uploadFile));

			// Create a new App.io app
			AppioAppObject testAppObject = testService.createApp(appName);

			// Add a new version
			AppioVersionObject testVersionObject = testService.addVersion(
					testAppObject.getId(), fileUrl);

			// Get app info and check for new version id
			testAppObject = testService.findApp(appName);

			assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
			assertEquals(testAppObject.getVersion_ids()[0],
					testVersionObject.getId());

			// Cleanup: delete the app object
			Thread.sleep(5000);
			testService.deleteApp(testAppObject.getId());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
