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

import java.io.File;
import java.io.Serializable;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * @author markprichard
 * 
 */
public class S3Service implements Serializable {
	private static final long serialVersionUID = 1L;

	private AmazonS3 s3client = null;

	public S3Service(String accessKey, String secretKey) {
		super();
		s3client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
	}

	/**
	 * @param bucketName
	 * @param keyName
	 * @param uploadFile
	 * @return
	 */
	public String getUploadUrl(String bucketName, String keyName, File uploadFile) throws AmazonServiceException,
			AmazonClientException {

		try {
			s3client.putObject(new PutObjectRequest(bucketName, keyName, uploadFile)
					.withCannedAcl(CannedAccessControlList.PublicRead));

		} catch (AmazonServiceException ase) {
			LOGGER.fine("AmazonServiceException");
			LOGGER.fine("Error Message:    " + ase.getMessage());
			LOGGER.fine("HTTP Status Code: " + ase.getStatusCode());
			LOGGER.fine("AWS Error Code:   " + ase.getErrorCode());
			LOGGER.fine("Error Type:       " + ase.getErrorType());
			LOGGER.fine("Request ID:       " + ase.getRequestId());
			throw ase;
		} catch (AmazonClientException ace) {
			LOGGER.fine("AmazonClientException");
			LOGGER.fine("Error Message: " + ace.getMessage());
			throw ace;
		}

		String s3PublicUrl = "https://s3.amazonaws.com/" + bucketName + "/" + keyName;
		LOGGER.fine("S3 public URL: " + s3PublicUrl);
		return s3PublicUrl;
	}
	
    private static final Logger LOGGER = Logger.getLogger(S3Service.class.getName());
}
