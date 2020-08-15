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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import com.palmergames.spigot.permtrigger.PermTrigger;
import com.palmergames.spigot.permtrigger.triggers.TriggerTest;

/**
 * @author ElgarL
 *
 */
public abstract class PermissionSource {

	private PermTrigger plugin;

	/**
	 * Constructor
	 * 
	 * @param instance	PermissionTriggers reference.
	 */
	PermissionSource(PermTrigger instance) {

		this.plugin = instance;
		
		PluginManager manager = getPlugin().getServer().getPluginManager();

		manager.registerEvents(new PlayerEvents(), getPlugin());
	}
	
	/**
	 * @return the PermissionTriggers instance
	 */
	PermTrigger getPlugin() {

		return plugin;
	}
	
	/**
	 * Check our triggers for this Player to see if any need to activate (ThreadSafe)
	 * All checks are performed in a Future, Synchronised with the main thread.
	 * 
	 * @param player	the player to permission test.
	 */
	public void checkTriggers(Player player) {
		
		/*
		 * Test in a Future that executes on the main thread as this
		 * method will be accessed by asynchronous Threads.
		 * We don't care about the result so don't bother to store the future
		 */
		getPlugin().getServer().getScheduler().callSyncMethod(getPlugin(), new TriggerTest(getPlugin(), player));
	}
	
	/**
	 * Disable anything we need to before shutdown.
	 */
	public abstract void disable();
	
	/**
	 * Is the PlayerLoginEvent enabled?
	 * 
	 * @return
	 */
	abstract boolean PlayerLoginEvent();
	
	/**
	 * Is the PlayerChangedWorldEvent enabled?
	 * 
	 * @return
	 */
	abstract boolean PlayerChangedWorldEvent();
	
	/**
	 * Is the PlayerQuitEvent enabled?
	 * 
	 * @return
	 */
	abstract boolean PlayerQuitEvent();
	
	/**
	 * Player events tracked to monitor permissions
	 * 
	 * @author ElgarL
	 * 
	 */
	protected class PlayerEvents implements Listener {

		/*
		 * Player Joined the server.
		 */
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerJoin(PlayerJoinEvent event) {
			
			if (!PlayerLoginEvent()) return;
			
			checkTriggers(event.getPlayer());
		}
		
		/*
		 * Player changed worlds
		 */
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

			if (!PlayerChangedWorldEvent()) return;
			
			checkTriggers(event.getPlayer());
		}

		/*
		 * Stop tracking a player who leaves.
		 */
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerQuit(PlayerQuitEvent event) {

			if (!PlayerQuitEvent()) return;
			
			Player player = event.getPlayer();
			
			// Check for any old/unknown triggers as they are no longer online.
			getPlugin().getDatabase().getDataCache().purgeOldTriggers(player.getUniqueId());

		}
	}
}
