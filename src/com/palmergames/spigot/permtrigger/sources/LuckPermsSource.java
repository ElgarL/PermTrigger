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

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;

import com.palmergames.spigot.permtrigger.PermTrigger;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.track.mutate.TrackMutateEvent;
import net.luckperms.api.event.user.track.UserTrackEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;

/**
 * @author ElgarL
 *
 */
public class LuckPermsSource extends PermissionSource {

	private LuckPerms plugin;
	private Set<EventSubscription<?>> events = new HashSet<EventSubscription<?>>();
	
	/**
	 * Constructor
	 * 
	 * @param instance	PermissionTriggers reference.
	 * @param plugin	LuckPerms reference.
	 */
	public LuckPermsSource(PermTrigger instance, Object plugin) {
		
		super(instance);
		this.plugin = (LuckPerms)plugin;
		
		try {
			LPEventListener listener = new LPEventListener();
			
			events.add(getPluginInstance().getEventBus().subscribe(NodeMutateEvent.class,listener::onNodeMutateEvent));
			events.add(getPluginInstance().getEventBus().subscribe(UserTrackEvent.class,listener::onUserTrackEvent));
			events.add(getPluginInstance().getEventBus().subscribe(TrackMutateEvent.class,listener::onTrackMutateEvent));
			
		} catch (IllegalPluginAccessException e) {
			System.out.print("Your Version of LuckPerms is incompatible. Please update.");
		}
	}
	
	@Override
	public void disable() {

		for (EventSubscription<?> eventSubscription : events) {
			eventSubscription.close();
		}
	}
	
	/**
	 * @return the LuckPerms Instance
	 */
	private LuckPerms getPluginInstance() {

		return plugin;
	}
	
	/**
	 * Returns a class object for LuckPerms.
	 * 
	 * @return	class or null.
	 */
	public static Class<?> getClazz() {
		
		try {
			return LuckPerms.class;
		} catch (NoClassDefFoundError ex) {
			// Ignore the error as it simply means the plug-in is not on the server.
		}
		return null;
	}
	
	@Override
	boolean PlayerLoginEvent() {

		return true;
	}

	@Override
	boolean PlayerChangedWorldEvent() {

		return true;
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
	class LPEventListener implements Listener {
		
		/*
		 * A node was changed on the player or in a group.
		 */
		@EventHandler(priority = EventPriority.LOW)
		public void onNodeMutateEvent(NodeMutateEvent event) {
			
			User user;
			
			/*
			 * A node has changed on a player or they have had groups added/removed.
			 */
			if (event.isUser()) {
				
				user = (User)event.getTarget();
				Player player = getPlugin().getServer().getPlayer(user.getUniqueId());
				
				if ((player != null) && (player.isOnline()))
					checkTriggers(player);
			}
			
			/*
			 * A groups was created/changed/deleted.
			 */
			if (event.isGroup()) {
				
				Group group = (Group)event.getTarget();
				QueryOptions options = QueryOptions.builder(QueryMode.CONTEXTUAL).build();
				
				for (Player player : getPlugin().getServer().getOnlinePlayers()) {
					
					user = getPluginInstance().getUserManager().getUser(player.getUniqueId());
					
					/*
					 * If the user has this group as their main, or inherits it.
					 */
					if ((user.getPrimaryGroup().equals(group.getName())) || (user.getInheritedGroups(options).contains(group))) {
						
						if ((player != null) && (player.isOnline()))
							checkTriggers(player);
					}
				}
			}
		}
		
		/**
		 * User moved up or down a track.
		 * 
		 * @param event
		 */
		@EventHandler(priority = EventPriority.LOW)
		public void onUserTrackEvent(UserTrackEvent event) {
			
			User user = event.getUser();
			Player player = getPlugin().getServer().getPlayer(user.getUniqueId());
			
			if ((player != null) && (player.isOnline()))
				checkTriggers(player);
		}
		
		/**
		 * A change was made to a track.
		 * 
		 * @param event
		 */
		@EventHandler(priority = EventPriority.LOW)
		public void onTrackMutateEvent(TrackMutateEvent event) {
			
			/*
			 * Check all online players because deciphering the tracks,
			 * groups and inheritance would be an absolute nightmare.
			 */
			for (Player player : getPlugin().getServer().getOnlinePlayers()) {
				if (player.isOnline())
					checkTriggers(player);
			}
		}
	}
}
