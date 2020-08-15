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
package com.palmergames.spigot.permtrigger.triggers;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.palmergames.spigot.permtrigger.Database;
import com.palmergames.spigot.permtrigger.PermTrigger;

/**
 * A Future to be run on the main thread.
 * 
 * @author ElgarL
 *
 */
public class TriggerTest implements Callable<Boolean>{
	
	private PermTrigger plugin;
	private Player player;
	private Logger logger;
	
	public TriggerTest(PermTrigger plugin, Player player) {
		
		this.plugin = plugin;
		this.player = player;
		this.logger = plugin.getLogger();
	}

	@Override
	public Boolean call() throws Exception {

		if (!plugin.getServer().isPrimaryThread()) {
			plugin.getLogger().severe("*** WARNING NOT MainThread execution ***");
			return false;
		}
		
		/*
		 * Abort if the player is not online.
		 */
		if (!player.isOnline())
			return false;
		
		Database database = plugin.getDatabase();
		
		boolean state;
		
		for (TriggerNode node : database.getTriggers()) {
				
			String triggerPerm = node.getPermission();
			
			state = database.getDataCache().hasTrigger(player.getUniqueId(), triggerPerm);
				
			if (player.isPermissionSet(triggerPerm)) {
				
				if (!state || node.isForced()) {
					/*
					 * The Player gained this permission.
					 * or is a new player and should have defaults set.
					 */
					logger.info(String.format("%s trigger added! %s.", player.getName(), triggerPerm));
					
					for (String commandLine : node.getAdded())
						parseCommand(commandLine);
						
					database.getDataCache().setTriggerState(player.getUniqueId(), triggerPerm, true);
				}
					
			} else if (state || node.isForced()) {
				
				/*
				 * The Player lost this permission.
				 */
				plugin.getLogger().info(String.format("%s trigger removed! %s.", player.getName(),triggerPerm));
				
				for (String commandLine : node.getRemoved())
					parseCommand(commandLine);
					
				database.getDataCache().setTriggerState(player.getUniqueId(), triggerPerm, false);
			}
		}
		
		return true;
	}
	
	/**
	 * Parse the command to discern its type and how we should process it.
	 * 
	 * @param command
	 */
	private void parseCommand(String command) {
		
		plugin.getLogger().info(String.format("Processing command: %s", command));
		
		/*
		 * Perform all replacements.
		 */
		command = command.replace("{player}", player.getName());
		
		/*
		 * Extract command type.
		 */
		switch (command.split(" ")[0]) {
		
		case "#broadcast":
			
			broadcast(command.replaceFirst("#broadcast ", ""));
			break;
			
		case "#tell":
			
			tell(command.replaceFirst("#tell ", ""));
			break;
			
		default:
			
			// Default console command.
			console(command);
		}
	}
	
	/**
	 * Broadcast this message to the whole server.
	 * 
	 * @param message
	 */
	private void broadcast(String message) {
		
		plugin.getServer().broadcastMessage(message);
	}
	
	/**
	 * Send a personal message to the player.
	 * 
	 * @param message
	 */
	private void tell(String message) {
		
		player.sendMessage(message);
	}
	
	/**
	 * Issue this command at the console.
	 * 
	 * @param command
	 */
	private void console(String command) {
		
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
	}

}
