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
package com.palmergames.spigot.permtrigger.data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.palmergames.spigot.permtrigger.Database;
import com.palmergames.spigot.permtrigger.triggers.TriggerNode;
import com.palmergames.spigot.permtrigger.utils.FileUtils;


/**
 * @author ElgarL
 *
 */
public class Json extends IOBase {
	
	String JsonFileName = "perm_triggers.json";
	
	/**
	 * Constructor for saving/loading Json Data.
	 * 
	 * @param db reference to the Database class.
	 * @param load true for load or false for save
	 */
	public Json(Database db, boolean load) {

		super(db, load);
	}
	
	@Override
	protected boolean load() {
		
		 
		File triggerFile = new File(getFolder(), JsonFileName);

		/*
		 * Create the triggers Json file if it doesn't exist.
		 */
		if (!triggerFile.exists()) {
			try {
				FileUtils.copy(getDatabase().getPlugin().getResource(JsonFileName), triggerFile);
			} catch (IOException ex) {
				getLogger().severe(String.format("ERROR: creating file - %s", JsonFileName));
			}
		}
		
		/*
		 * Attempt to read the triggers Json file.
		 */
		String data = FileUtils.fileToString(triggerFile);
		
		if (data.isEmpty()) {
			getLogger().warning("No file or no data found!");
			return false;
		}
		return parse(data);
	}
	
	protected boolean parse(String data) {
		
		Set<TriggerNode> triggers = new CopyOnWriteArraySet<TriggerNode>();
		
		try {
			JsonElement obj = new JsonParser().parse(data);
			
			/*
			 * Attempt to parse all trigger data
			 */
			for (Entry<String, JsonElement> node : obj.getAsJsonObject().entrySet()) {
				
				String perm = node.getKey();
				boolean forced = false;
				JsonArray added = new JsonArray();
				JsonArray removed = new JsonArray();
				
				/*
				 * parse any added and removed command arrays for this trigger node.
				 */
				for (Entry<String, JsonElement> values : node.getValue().getAsJsonObject().entrySet()) {
					
					JsonElement value = values.getValue();
					
					if (values.getKey().equalsIgnoreCase("forced"))
						if (value.isJsonPrimitive())
							forced = value.getAsBoolean();
					
					if (values.getKey().equalsIgnoreCase("added"))
						if (value.isJsonArray()) {
							added = value.getAsJsonArray();
						} else {
							added.add(value.getAsString());
						}
					
					if (values.getKey().equalsIgnoreCase("removed"))
						if (value.isJsonArray()) {
							removed = value.getAsJsonArray();
						} else {
							removed.add(value.getAsString());
						}
				}
				
				triggers.add(new TriggerNode((String) perm, JsonToArray(added), JsonToArray(removed), forced));
			}
			
			if (!triggers.isEmpty())
				getLogger().info(String.format("(%s) Triggers updated.", triggers.size()));
			
			getDatabase().setTriggers(triggers);
			return true;
		
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	protected boolean save() {
		
		try {
			String data = "# This file contains permission triggers.\n"
					+ "# \n"
					+ "# Triggers are sets of commands to be issue at the console\n"
					+ "# when a permission is added to, or removed from a player.\n"
					+ "# \n"
					+ "# Commands can have prefixes to perform specific tasks.\n" 
					+ "# '#broadcast' '#tell'\n";
					
			data = data + WriteString();
			
			FileUtils.stringToFile(data, new File(getFolder(), JsonFileName));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Generate a String that we can save to a file from the Database Triggers.
	 * 
	 * @param out a BufferedWriter to write to.
	 * @throws Exception
	 */
	protected String WriteString() throws Exception {
		
		Set<TriggerNode> triggers = getDatabase().getTriggers();
		
		JsonObject list = new JsonObject();
		
		if (triggers != null) {
			
			for (TriggerNode node : triggers) {
				
				JsonObject obj = new JsonObject();
				
				obj.addProperty("forced", node.isForced());
				obj.add("added", ListToJsonArray(node.getAdded()));
				obj.add("removed", ListToJsonArray(node.getRemoved()));
				
				list.add(node.getPermission(), obj);
			}
		}
		
		/*
		 * Write a formatted string to be human readable.
		 */
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(list).toString();
	}
	
	@Override
	public void run() {

		if (isLoad())
			load();
		else
			save();
	}
	
	/**
	 * Convert a JsonArray to a List<String>
	 * 
	 * @param array	JsonArray to convert
	 * @return		LinkedList<String>
	 */
	private List<String> JsonToArray(JsonArray array) {
		
		List<String> list = new LinkedList<String>();
		for (JsonElement element : array) {
			list.add(element.getAsString());
		}
		return list;
	}
	
	/**
	 * Convert a List<String> to a JsonArray
	 * 
	 * @param list
	 * @return
	 */
	private JsonArray ListToJsonArray(List<String> list) {
		
		JsonArray array = new JsonArray();
		for (String element : list) {
			array.add(element);
		}
		return array;
	}

	
}
