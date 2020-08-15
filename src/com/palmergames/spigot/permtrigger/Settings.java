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
package com.palmergames.spigot.permtrigger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author ElgarL
 *
 */
public class Settings {

	private PermTrigger plugin;
	private FileConfiguration config;
	
	String type;
	String URL;
	boolean refresh;
	Long interval;

	/**
	 * 
	 */
	public Settings(PermTrigger plugin) {

		this.plugin = plugin;
	}
	
	public boolean load() {
		
		plugin.saveDefaultConfig();
		/*
		 * Ensure we load the config clean in case of a reload.
		 */
		plugin.reloadConfig();
		config = plugin.getConfig();
		
		ConfigurationSection section = config.getConfigurationSection("data");
		if (section == null) return false;
		
		type = section.getString("type");
		URL = section.getString("URL");
		
		section = section.getConfigurationSection("refresh");
		if (section == null) return false;
		
		refresh = section.getBoolean("enabled");
		interval = section.getLong("interval");
		
		return (type != null) && (URL != null) && (interval != null);
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
	
		return type.toUpperCase();
	}
	
	/**
	 * @return the URL
	 */
	public String getURL() {
	
		return URL;
	}
	
	/**
	 * @return the refresh
	 */
	public boolean isRefresh() {
	
		return refresh;
	}
	
	/**
	 * @return the interval
	 */
	public Long getInterval() {
	
		return interval;
	}
}
