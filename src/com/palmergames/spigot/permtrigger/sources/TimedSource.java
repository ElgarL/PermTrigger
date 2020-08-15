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
import org.bukkit.scheduler.BukkitTask;

import com.palmergames.spigot.permtrigger.PermTrigger;

/**
 * @author ElgarL
 *
 */
public class TimedSource extends PermissionSource {

	private BukkitTask task = null;
	
	public TimedSource(PermTrigger instance) {
		
		super(instance);
		
		startTimer();
	}
	
	@Override
	public void disable() {
		
		task.cancel();
	}
	
	/**
	 * Start a repeating asynchronous task to check all online players.
	 */
	private void startTimer() {

		task = getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				/*
				 * Check all online players for triggers.
				 */
				for (Player player : getPlugin().getServer().getOnlinePlayers()) {
					
					checkTriggers(player);
				}
			}
		}, 1, 1200); // 1 minute repeat.
		
	}

	@Override
	public boolean PlayerLoginEvent() {

		return true;
	}

	@Override
	public boolean PlayerChangedWorldEvent() {

		return true;
	}

	@Override
	public boolean PlayerQuitEvent() {

		return true;
	}
	
	
}
