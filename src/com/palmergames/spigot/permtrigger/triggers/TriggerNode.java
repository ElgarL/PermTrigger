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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ElgarL
 *
 */
public class TriggerNode {
	
	private String permission;
	private List<String> added = new ArrayList<String>();
	private List<String> removed = new ArrayList<String>();
	private boolean forced = false;
	
	/**
	 * Constructor
	 * 
	 * @param permission
	 */
	public TriggerNode(String permission) {
		
		this.permission = permission;
	}
	
	public TriggerNode(String permission, Collection<? extends String> added, Collection<? extends String> removed, boolean forced) {
		
		this.permission = permission;
		this.added.addAll(added);
		this.removed.addAll(removed);
		this.forced = forced;
	}

	/**
	 * The permission that this node triggers on.
	 * 
	 * @return the permission
	 */
	public String getPermission() {
	
		return permission;
	}
	
	/**
	 * Commands to execute when this permission is added to a player.
	 * 
	 * @return the added List<String>
	 */
	public List<String> getAdded() {
	
		return added;
	}
	
	/**
	 * Commands to execute when this permission is removed from a player.
	 * 
	 * @return the removed List<String>
	 */
	public List<String> getRemoved() {
	
		return removed;
	}
	
	/**
	 * Replace the enabled command list.
	 * 
	 * @param added the added commands to set
	 */
	public void setAdded(List<String> added) {
	
		this.added = added;
	}
	
	/**
	 * Replace the removed command list.
	 * 
	 * @param removed the removed commands to set
	 */
	public void setRemoved(List<String> removed) {
	
		this.removed = removed;
	}
	
	/**
	 * Add a single command.
	 * 
	 * @param command
	 */
	public void addAdded(String command) {
		
		added.add(command);
	}
	
	/**
	 * Add a single command.
	 * 
	 * @param command
	 */
	public void addRemoved(String command) {
		
		removed.add(command);
	}
	
	public void setForced(boolean forced) {
		
		this.forced = forced;
	}
	
	public boolean isForced() {
		
		return forced;
	}
}
