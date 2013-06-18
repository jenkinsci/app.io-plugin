package org.jenkinsci.plugins.appio.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author markprichard
 * 
 */
public class ZipService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private Logger logger = null;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	static interface Logger {
		void logDebug(String message);
	}

	private void logDebug(String message) {
		if (logger != null) {
			logger.logDebug(message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * @param fileToZip
	 * @param zipFile
	 * @param excludeContainingFolder
	 * @throws IOException
	 */
	public void zipFile(String fileToZip, String zipFile,
			boolean excludeContainingFolder) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(
				zipFile));

		File srcFile = new File(fileToZip);
		if (excludeContainingFolder && srcFile.isDirectory()) {
			for (String fileName : srcFile.list()) {
				addToZip("", fileToZip + "/" + fileName, zipOut);
			}
		} else {
			addToZip("", fileToZip, zipOut);
		}

		zipOut.flush();
		zipOut.close();

		logDebug("Successfully created " + zipFile);
	}

	private void addToZip(String path, String srcFile, ZipOutputStream zipOut)
			throws IOException {
		File file = new File(srcFile);
		String filePath = "".equals(path) ? file.getName() : path + "/"
				+ file.getName();
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				addToZip(filePath, srcFile + "/" + fileName, zipOut);
			}
		} else {
			logDebug("Added: " + filePath);
			zipOut.putNextEntry(new ZipEntry(filePath));
			FileInputStream in = new FileInputStream(srcFile);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}

			in.close();
		}
	}
}