package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

public class S3ServiceTest {

	// Test properties loaded via getClassLoader().getResourceAsStream()
	private String propertyPackage = ("org/jenkinsci/plugins/appio/");
	private String propertyFile = propertyPackage + "test.properties";

    private String accessKey = null;
    private String secretKey = null;
	private String bucketName = null;
	private String keyName = null;
	private File uploadFile = null;
	private String badBucket = null;
	private File badFile = null;

	private Properties testProperties = new Properties();
	
	// Set logging levels
	static {
		Logger l = Logger.getLogger(S3Service.class.getName());
		l.setLevel(Level.ALL);
		ConsoleHandler h = new ConsoleHandler();
		h.setLevel(Level.ALL);
		l.addHandler(h);
	}

	public S3ServiceTest() {
		super();
		loadTestProperties();
	}

	// Utility to load test properties
	public void loadTestProperties() {
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(propertyFile);
		try {
			testProperties.load(in);

			accessKey = testProperties.getProperty("S3.accessKey");
            secretKey = testProperties.getProperty("S3.secretKey");
			bucketName = testProperties.getProperty("S3.bucketName");
			keyName = testProperties.getProperty("S3.keyName");
			uploadFile = new File(testProperties.getProperty("S3.uploadFile"));
			badFile = new File(testProperties.getProperty("S3.badFile"));
			badBucket = testProperties.getProperty("S3.badBucket");
			
			System.out.println("Using test properties from: " + propertyFile);
			System.out.println("S3.accessKey = " + accessKey);
			System.out.println("S3.secretKey = " + secretKey);
			System.out.println("S3.bucketName = " + bucketName);
			System.out.println("S3.keyName = " + keyName);
			System.out.println("S3.uploadFile = " + uploadFile);
			System.out.println("S3.badFile = " + badFile);
			System.out.println("S3.badBucket = " + badBucket);

		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void getUploadUrl() {
		S3Service s3Service = new S3Service(accessKey, secretKey);
		String testResult = null;

		try {
			testResult = s3Service
					.getUploadUrl(bucketName, keyName, uploadFile);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(testResult, "https://s3.amazonaws.com" + "/" + bucketName
				+ "/" + keyName);
	}

	@Test
	public void getUploadURLBadPath() {
		S3Service s3Service = new S3Service(accessKey, secretKey);
		String testResult = null;

		try {
			testResult = s3Service.getUploadUrl(bucketName, keyName, badFile);
		} catch (Exception e) {
			assertTrue(e.getMessage(), true);
		}
		assertEquals(testResult, null);
	}

	@Test
	public void getUploadURLBadBucket() {
		S3Service s3Service = new S3Service(accessKey, secretKey);
		String testResult = null;

		try {
			testResult = s3Service.getUploadUrl(badBucket, keyName, uploadFile);
		} catch (Exception e) {
			assertTrue(e.getMessage(), true);
		}
		assertEquals(testResult, null);
	}
}
