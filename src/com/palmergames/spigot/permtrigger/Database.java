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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.palmergames.spigot.permtrigger.data.Http;
import com.palmergames.spigot.permtrigger.data.Json;
import com.palmergames.spigot.permtrigger.triggers.TriggerNode;

/**
 * @author ElgarL
 *
 */
public class Database {

	private PermTrigger plugin;
	private DataCache dataCache;
	
	private Set<TriggerNode> triggers = new CopyOnWriteArraySet<TriggerNode>();
	private Set<String> keys = new CopyOnWriteArraySet<String>();
	
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> future;

	/**
	 * Constructor.
	 * 
	 * @param plugin	PermissionTriggers reference.
	 */
	public Database(PermTrigger plugin) {

		this.plugin = plugin;
		dataCache = new DataCache(plugin);
		
		enable();
		
	}
	
	public void enable() {
		
		Long repeat = TimeUnit.MINUTES.toMinutes(plugin.getSettings().getInterval());
		
		Runnable task;
		
		switch (plugin.getSettings().getType()) {
			
		case "HTTP":
			
			task = new Http(this, true);
			break;
			
		default: // JSON
			
			task = new Json(this, true);
			break;
		}
		
		/*
		 * Start a ScheduledFuture to load triggers.
		 */
		if (plugin.getSettings().isRefresh()) {
			future = executor.scheduleAtFixedRate(task, 0, repeat, TimeUnit.MINUTES);
		} else {
			future = executor.schedule(task, 0, TimeUnit.MINUTES);
		}
	}
	
	public void disable() {
		
		if (future != null) {
			future.cancel(true);
		}
	}

	/**
	 * @return the plugin
	 */
	public PermTrigger getPlugin() {

		return plugin;
	}
	
	/**
	 * @return the triggers
	 */
	public Set<TriggerNode> getTriggers() {
	
		return Collections.unmodifiableSet(triggers);
	}
	
	/**
	 * Get a Set of trigger keys.
	 * 
	 * @return
	 */
	public Set<String> getTriggerKeys() {
		
		return Collections.unmodifiableSet(keys);
	}
	
	/**
	 * Replace all triggers with a new set.
	 * 
	 * @param triggers	CopyOnWriteArraySet<TriggerNode>
	 */
	public void setTriggers(Set<TriggerNode> triggers) {
		
		this.triggers = triggers;
		
		/*
		 * Copy the keys for faster tests later.
		 */
		Set<String> tmp = new CopyOnWriteArraySet<String>();
		
		for (TriggerNode triggerNode : triggers) {
			tmp.add(triggerNode.getPermission());
		}
		
		this.keys = tmp;
	}
	
	/**
	 * Fetch the Data cache (PersistentDataHandler).
	 * 
	 * @return the dataCache
	 */
	public DataCache getDataCache() {
	
		return dataCache;
	}
}
