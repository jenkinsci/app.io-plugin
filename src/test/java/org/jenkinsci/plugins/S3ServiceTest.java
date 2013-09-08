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

package org.jenkinsci.plugins;

import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Mark Prichard
 */
public class S3ServiceTest {

	// Test properties loaded from properties file
    private static final String propertyFile = "test.properties";

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
		try {
            testProperties.load(new FileInputStream(propertyFile));

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
