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
package com.palmergames.spigot.permtrigger.sources;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;

import com.palmergames.spigot.permtrigger.PermTrigger;

/**
 * @author ElgarL
 *
 */
public class GroupManagerSource extends PermissionSource {
	
	private GroupManager plugin;
	
	/**
	 * Constructor
	 * 
	 * @param instance	PermissionTriggers reference.
	 * @param plugin	GroupManager reference.
	 */
	public GroupManagerSource(PermTrigger instance, Object plugin) {
		
		super(instance);
		this.plugin = (GroupManager)plugin;
		
		try {
			getPlugin().getServer().getPluginManager().registerEvents(new GMEventListener(), getPlugin());
		} catch (IllegalPluginAccessException e) {
			System.out.print("Your Version of GroupManager is incompatible. Please update to a version by ElgarL.");
		}
	}
	
	@Override
	public void disable() {

	}

	/**
	 * @return the GroupManager Instance
	 */
	public GroupManager getPluginInstance() {

		return plugin;
	}
	
	/**
	 * Returns a class object for GroupManager.
	 * 
	 * @return	class or null.
	 */
	public static Class<?> getClazz() {
		
		try {
			return GroupManager.class;
		} catch (NoClassDefFoundError ex) {
			// Ignore the error as it simply means the plug-in is not on the server.
		}
		return null;
	}
	
	@Override
	boolean PlayerLoginEvent() {

		return false;
	}

	@Override
	boolean PlayerChangedWorldEvent() {

		return false;
	}

	@Override
	boolean PlayerQuitEvent() {

		return true;
	}

	/**
	 * Package Protected Listener
	 * 
	 * @author ElgarL
	 *
	 */
	class GMEventListener implements Listener {

		public GMEventListener() {}

		@EventHandler(priority = EventPriority.LOW)
		public void onGMUserEvent(GMUserEvent event) {
			
			User user = event.getUser();
			Player player = (user != null)? user.getBukkitPlayer() : null;

			try {
				switch (event.getAction()) {
				
				case USER_PERMISSIONS_CHANGED:
					
					if (player != null) {
						checkTriggers(player);
					}
					
					break;
					
				default:
					
					break;
				}
				
			} catch (IllegalArgumentException e) {
				// Not tracking this event type
			}
		}
	}
}
