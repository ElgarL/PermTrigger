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
import java.util.concurrent.atomic.AtomicBoolean;

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
}
