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

import java.util.Set;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author ElgarL
 *
 */
public class DataCache {

	private NamespacedKey key;
	private PermTrigger plugin;
	private Player player;
	
	/**
	 * 
	 */
	public DataCache(PermTrigger plugin) {

		this.plugin = plugin;
	}
	
	/**
	 * Set the state of a Trigger. Or delete if false.
	 * 
	 * @param uid	UUID of the Player
	 * @param triggerPerm	String permission to trigger on.
	 * @param state	true or false.
	 */
	public void setTriggerState(UUID uid, String triggerPerm, boolean state) {
		
		player = plugin.getServer().getPlayer(uid);
		key = new NamespacedKey(plugin, triggerPerm);
		
		if (player == null)
			return;
		
		if (state == true)
			player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
		else
			player.getPersistentDataContainer().remove(key);
	}
	
	/**
	 * Is this trigger currently set on the Player.
	 * 
	 * @param uid	UUID of the Player
	 * @param triggerPerm	String permission to trigger on.
	 * @return	true if set.
	 */
	public boolean hasTrigger(UUID uid, String triggerPerm) {
		
		player = plugin.getServer().getPlayer(uid);
		key = new NamespacedKey(plugin, triggerPerm);
		
		if (player == null)
			return false;

		Byte node = player.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
		
		if (node != null)
			return node.byteValue() == 1;
		
		return false;
	}
	
	/**
	 * Called when a player disconnects to purge any old/unknown triggers.
	 * 
	 * @param uid	UUID of the Player
	 */
	public void purgeOldTriggers(UUID uid) {
		
		player = plugin.getServer().getPlayer(uid);
		
		if (player == null)
			return;
		
		Set<String> triggers = plugin.getDatabase().getTriggerKeys();
		
		PersistentDataContainer container = player.getPersistentDataContainer();
		
		for (NamespacedKey keys : container.getKeys()) {
			if (!triggers.contains(keys.getKey())) {
				plugin.getLogger().warning(String.format("Unknown trigger removed! %s.", keys.getKey()));
				container.remove(keys);
			}
		}

		
	}
}
