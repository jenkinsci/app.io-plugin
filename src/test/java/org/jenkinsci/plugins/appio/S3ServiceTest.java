package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
