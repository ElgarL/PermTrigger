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

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.spigot.permtrigger.commands.PTReload;
import com.palmergames.spigot.permtrigger.metrics.BStats;
import com.palmergames.spigot.permtrigger.sources.GroupManagerSource;
import com.palmergames.spigot.permtrigger.sources.LuckPermsSource;
import com.palmergames.spigot.permtrigger.sources.PermissionSource;
import com.palmergames.spigot.permtrigger.sources.TimedSource;

/**
 * @author ElgarL
 *
 */
public class PermTrigger extends JavaPlugin {

	private static PermTrigger instance;
	private String version;
	
	private PermissionSource permissionSource;
	private Database database;
	private Settings settings;

	/**
	 * 
	 */
	public PermTrigger () {
		
		/*
		 * Initialize everything we can here
		 * but do NOTHING that attempts to load
		 * any data, nor access any other plug-ins.
		 */
		
		// Initialize our instance.
		instance = this;
		
		version = instance.getDescription().getVersion();
	}
	
	@Override
	public void onLoad() {

		/*
		 * Load any data here we need from files or
		 * non Bukkit data sources (not other plug-ins).
		 */
		super.onLoad();
		
		settings = new Settings(this);
		
		if (!settings.load()) {
			getLogger().severe("Disabling: Error loading config.");
			this.getPluginLoader().disablePlugin(instance);
			return;
		}
		
		/*
		 * Setup our database and load our data (Async).
		 */
		if (database != null) {

			/*
			 * Perform a reload of triggers as we are restarting.
			 */
			database.disable();
			database.enable();
			
		} else {
			database = new Database(this);
			
			/*
			 * Register BStats
			 */
			try {
				new BStats(this, 8530);
			    
			} catch (Exception e) {
				getLogger().warning(" Error setting up metrics");
			}
		}
	}
	
	@Override
	public void onEnable() {
		
		/*
		 *  Executes after the worlds have initialized.
		 */
		super.onEnable();
		
		// TODO: register any commands after finishing all loading.
		
		getLogger().info("================================================================");
		
		// Setup our permission source
		checkPlugins();
		
		getLogger().info(" Mod Version: " + version + " - Enabled.");
		getLogger().info(String.format(" (Load:%s) (Refresh:%s) (Interval:%d minutes) (T:%d)"
				, this.getSettings().type
				, ((this.getSettings().refresh)? "Enabled" : "Disabled")
				, this.getSettings().getInterval()
				, database.getTriggers().size()));
		getLogger().info("================================================================");
		
		/*
		 * Run a task after this load has finished to re-test all online players (restart).
		 */
		getInstance().getServer().getScheduler().runTaskLater(getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				for (Player player : getInstance().getServer().getOnlinePlayers()) {
					getLogger().info(String.format("Rechecking Triggers for %s as we must have reloaded.", player.getName()));
					getPermissionSource().checkTriggers(player);
				}
			}
		}, 1);
		
		/*
		 * Register our commands.
		 */
		getCommand("ptreload").setExecutor(new PTReload(getInstance()));
	}
	
	public void reLoad() {
		
		onDisable();
		onLoad();
		onEnable();
	}

	@Override
	public void onDisable() {
		
		if (getPermissionSource() != null)
			getPermissionSource().disable();
		
		if (database != null)
			database.disable();
	}
	
	/**
	 * Attempt to hook permission plug-ins that generate supported events.
	 * Otherwise use a basic timer check.
	 */
	private void checkPlugins() {
		
		RegisteredServiceProvider<?> provider = null;
		
		/*
		 * Test for GroupManager
		 */
		provider = getServer().getServicesManager().getRegistration(GroupManagerSource.getClazz());
		
		if (provider != null) {
			getLogger().info(String.format(" Using %s Version: %s", provider.getPlugin().getName(), provider.getPlugin().getDescription().getVersion()));
			
			setPermissionSource(new GroupManagerSource(this, provider.getProvider()));
			return;
		}
		
		/*
		 * Test for LuckPerms
		 */
		provider = getServer().getServicesManager().getRegistration(LuckPermsSource.getClazz());
		
		if (provider != null) {
			getLogger().info(String.format(" Using %s Version: %s", provider.getPlugin().getName(), provider.getPlugin().getDescription().getVersion()));
			
			setPermissionSource(new LuckPermsSource(this, provider.getProvider()));
			return;
		}
		
		/*
		 * Enable timed testing.
		 */
		if (getPermissionSource() == null) {
			//No Permission source so use a timed check system.
			getLogger().info(String.format(" Using %s: %s", "Timed permission checks", "Enabled!"));
			setPermissionSource(new TimedSource(this));
		}

	}
	
	/**
	 * @return the database
	 */
	public Database getDatabase() {
	
		return database;
	}

	/**
	 * @return the Settings
	 */
	public Settings getSettings() {

		return settings;
	}

	/**
	 * @param source
	 */
	private void setPermissionSource(PermissionSource source) {
		
		this.permissionSource = source;
	}
	
	/**
	 * @return the permissionSource
	 */
	public PermissionSource getPermissionSource() {
	
		return permissionSource;
	}

	/**
	 * @return the instance
	 */
	
	public static PermTrigger getInstance(){
		
	    return instance;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {

		return version;
	}

}
