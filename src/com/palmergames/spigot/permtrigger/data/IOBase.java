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

import java.io.File;
import java.util.logging.Logger;

import com.palmergames.spigot.permtrigger.Database;


/**
 * @author ElgarL
 *
 */
public abstract class IOBase implements Runnable {
	
	private boolean load = true;
	private Database database = null;
	private Logger logger = null;
	private File folder = null;
	
	/**
	 * Constructor for saving/loading
	 * 
	 * @param db reference to the Database class.
	 * @param load load or save
	 */
	public IOBase(Database db, boolean load) {

		this.load = load;
		this.database = db;
		
		logger = db.getPlugin().getLogger();
		folder = getDatabase().getPlugin().getDataFolder();
	}

	/**
	 * Load Triggers.
	 */
	protected abstract boolean load();

	/**
	 * Save the Triggers to file.
	 * 
	 * @return
	 */
	protected abstract boolean save();
	
	/**
	 * Are we loading or saving
	 * 
	 * @return true if loading
	 */
	protected boolean isLoad() {
	
		return load;
	}
	
	/**
	 * Reference to the Database instance.
	 * 
	 * @return the database
	 */
	protected Database getDatabase() {
	
		return database;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {

		return logger;
	}

	
	/**
	 * Get the data folder for this plugin.
	 * 
	 * @return the folder
	 */
	public File getFolder() {
	
		return folder;
	}
	
}
