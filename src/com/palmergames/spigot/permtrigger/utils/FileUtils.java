/*
 *  PermTrigger - A plug-in for Spigot/Bukkit based Minecraft servers.
 *  Copyright (C) 2020  ElgarL
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.palmergames.spigot.permtrigger.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	private static final String encoding = "UTF-8";
	private static AtomicBoolean sync = new AtomicBoolean();

	public static String fileSeparator() {

		return System.getProperty("file.separator");
	}

	public static String lineSeperator() {

		return System.getProperty("line.separator");
	}

	/**
	 * Load a Stream into a String.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String streamToString(InputStream is) throws IOException {

		Writer writer = new StringWriter();

		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			reader.close();

		} finally {
			try {
				is.close();
			} catch (NullPointerException e) {
				//Failed to open a stream
				throw new IOException();
			}
		}
		return writer.toString();

	}

	/**
	 * Pass a resource name and it will return it's contents as a string
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String resourceToString(String name) throws IOException {

		if (name != null) {

			synchronized (sync) {
				InputStream is = FileUtils.class.getResourceAsStream(name);

				return streamToString(is);
			}
		} else {
			return "";
		}
	}

	/**
	 * Pass a file and it will return it's contents as a string.
	 * 
	 * @param file File to read.
	 * @return Contents of file. String will be empty in case of any errors.
	 */
	public static String fileToString(File file) {

		if (file != null && file.exists() && file.canRead()
				&& !file.isDirectory()) {
			Writer writer = new StringWriter();
			InputStream is = null;

			synchronized (sync) {
				char[] buffer = new char[1024];
				try {
					is = new FileInputStream(file);
					Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
					reader.close();
				} catch (IOException e) {
					System.out.println("Exception ");
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException ignore) {
						}
					}
				}
				return writer.toString();
			}
		} else {
			return "";
		}
	}

	/**
	 * Writes the contents of a string to a file
	 * making all newline codes platform specific.
	 * 
	 * @param source
	 * @param FileName
	 * @return
	 */
	public static boolean stringToFile(String source, String FileName) {

		if (source != null) {
			// Save the string to file
			try {
				return stringToFile(source, new File(FileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;

	}

	/**
	 * Writes the contents of a string to a file
	 * making all newline codes platform specific.
	 * 
	 * @param source String to write.
	 * @param file File to write to.
	 * @return True on success.
	 * @throws IOException
	 */
	public static boolean stringToFile(String source, File file) throws IOException {

		synchronized (sync) {
			try {

				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encoding);

				source = source.replaceAll("\n", lineSeperator());

				out.write(source);
				out.close();
				return true;

			} catch (IOException e) {
				System.out.println("Exception ");
				return false;
			}
		}
	}

	/**
	 * Write a list to a file, terminating each line with a system specific new line.
	 * 
	 * @param source
	 * @param targetLocation
	 * @return
	 * @throws IOException
	 */
	public static boolean listToFile(List<String> source, String targetLocation) throws IOException {

		synchronized (sync) {
			try {

				File file = new File(targetLocation);
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encoding);

				Iterator<String> itr = source.iterator();

				while (itr.hasNext())
					out.write(itr.next() + lineSeperator());

				out.close();
				return true;

			} catch (IOException e) {
				System.out.println("Exception ");
				return false;
			}
		}
	}

	/**
	 *  Copy a directory or file from one location to another.
	 *  
	 * @param sourceLocation
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

		// http://www.java-tips.org/java-se-tips/java.io/how-to-copy-a-directory-from-one-location-to-another-loc.html
		synchronized (sync) {
			if (sourceLocation.isDirectory()) {
				if (!targetLocation.exists())
					targetLocation.mkdir();

				String[] children = sourceLocation.list();
				for (int i = 0; i < children.length; i++)
					copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			} else {
				OutputStream out = new FileOutputStream(targetLocation);
				try {
					InputStream in = new FileInputStream(sourceLocation);
					// Copy the bits from in stream to out stream.
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0)
						out.write(buf, 0, len);
					in.close();
					out.close();
				} catch (IOException ex) {
					// failed to access file.
					System.out.println("Error: Could not access: "
							+ sourceLocation);
				}
				out.close();
			}
		}
	}
	
	/**
	 * Save an InputStream to File.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(InputStream src, File dst) throws IOException {

		synchronized (sync) {
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = src.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			try {
				src.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Move a file to a sub directory
	 * 
	 * @param sourceFile
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void moveFile(File sourceFile, String targetLocation) throws IOException {

		synchronized (sync) {
			if (sourceFile.isFile()) {
				// check for an already existing file of that name
				File f = new File((sourceFile.getParent() + fileSeparator()
						+ targetLocation + fileSeparator()
						+ sourceFile.getName()));
				if ((f.exists() && f.isFile()))
					f.delete();
				// Move file to new directory
				boolean success = sourceFile.renameTo(new File((sourceFile.getParent()
						+ fileSeparator()
						+ targetLocation), sourceFile.getName()));
				if (!success) {
					// File was not successfully moved
				}
			}
		}
	}

	/**
	 * Recursive Zip a single directory.
	 * 
	 * @param sourceFolder
	 * @param destination
	 * @throws IOException
	 */
	public static void zipDirectory(File sourceFolder, File destination) throws IOException {

		synchronized (sync) {
			ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
			recursiveZipDirectory(sourceFolder, output);
			output.close();
		}
	}

	/**
	 * Recursive Zip multiple directories.
	 * 
	 * @param sourceFolders
	 * @param destination
	 * @throws IOException
	 */
	public static void zipDirectories(File[] sourceFolders, File destination) throws IOException {

		synchronized (sync) {
			ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
			for (File sourceFolder : sourceFolders)
				recursiveZipDirectory(sourceFolder, output);
			output.close();
		}
	}

	private static void recursiveZipDirectory(File sourceFolder, ZipOutputStream zipStream) throws IOException {

		synchronized (sync) {

			String[] dirList = sourceFolder.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;

			for (int i = 0; i < dirList.length; i++) {

				File f = new File(sourceFolder, dirList[i]);

				if (f.isDirectory()) {
					recursiveZipDirectory(f, zipStream);
					continue;
				} else if (f.isFile() && f.canRead()) {

					FileInputStream input = new FileInputStream(f);
					ZipEntry anEntry = new ZipEntry(f.getPath());
					zipStream.putNextEntry(anEntry);

					while ((bytesIn = input.read(readBuffer)) != -1)
						zipStream.write(readBuffer, 0, bytesIn);

					input.close();
				}
			}
		}
	}

	/**
	 * Delete a file, or if it represents a directory,
	 * recursively delete it's contents beforehand.
	 */
	public static void deleteFile(File file) {

		synchronized (sync) {

			if (file.isDirectory()) {
				File[] children = file.listFiles();
				if (children != null) {
					for (File child : children)
						deleteFile(child);
				}
				children = file.listFiles();
				if (children == null || children.length == 0) {
					if (!file.delete())
						System.out.println("Error: Could not delete folder: "
								+ file.getPath());
				}
			} else if (file.isFile()) {
				if (!file.delete())
					System.out.println("Error: Could not delete file: "
							+ file.getPath());
			}
		}
	}

}
