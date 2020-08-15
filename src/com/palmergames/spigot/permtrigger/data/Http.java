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
package com.palmergames.spigot.permtrigger.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.palmergames.spigot.permtrigger.Database;
import com.palmergames.spigot.permtrigger.utils.FileUtils;

/**
 * @author ElgarL
 *
 */
public class Http extends Json {

	/**
	 * 
	 */
	public Http(Database db, boolean load) {

		super(db, load);
	}

	@Override
	protected boolean load() {
		
		URL url;
		URLConnection conn;
		InputStream stream;
		
		try {
			url = new URL(getDatabase().getPlugin().getSettings().getURL());
			
		} catch (MalformedURLException e) {
			getLogger().severe("ERROR: Malformed URL: " + e.getMessage());
			return false;
		}
		
		/*
		 * Attempt to connect.
		 */
		try {
			conn = url.openConnection();
			
		} catch (IOException e) {
			getLogger().severe("ERROR: Connection failure: " + e.getMessage());
			return false;
		}
					
		conn.setReadTimeout(5000);
		conn.addRequestProperty("User-Agent", "PermTrigger Update Check");
		conn.setDoOutput(true);
		
		try {
			stream = conn.getInputStream();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			String data = FileUtils.streamToString(stream);   
			parse(data);
				
			return true;
			
		} catch (IOException e) {
			getLogger().severe("ERROR: Unable to update from the specified address: " + e.getMessage());
		}

		return false;
	}
	
	@Override
	protected boolean save() {
		
		getLogger().warning("ERROR: A save was attempted while in HTTP mode!");
		
		return true;
	}
}
