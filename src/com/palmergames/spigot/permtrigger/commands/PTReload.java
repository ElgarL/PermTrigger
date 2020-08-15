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
package com.palmergames.spigot.permtrigger.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.spigot.permtrigger.PermTrigger;

/**
 * @author ElgarL
 *
 */
public class PTReload implements CommandExecutor {

	private PermTrigger plugin;
	private Logger logger;

	/**
	 * 
	 */
	public PTReload(PermTrigger plugin) {

		this.plugin = plugin;
		this.logger = getPlugin().getLogger();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		getLogger().info("Restarting...");
		
		if (sender instanceof Player)
			sender.sendMessage(String.format("[%s] Atempting to Reload...", getPlugin().getName()));
		
		getPlugin().reLoad();
		
		return true;
	}

	/**
	 * @return the plugin
	 */
	public PermTrigger getPlugin() {

		return plugin;
	}

	
	/**
	 * @return the logger
	 */
	public Logger getLogger() {
	
		return logger;
	}

}
