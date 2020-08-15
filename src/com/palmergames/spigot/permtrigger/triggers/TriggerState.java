/*
 * 
 */
package com.palmergames.spigot.permtrigger.triggers;


/**
 * @author ElgarL
 *
 */
public class TriggerState {

	private String trigger;
	private boolean state;
	
	/**
	 * 
	 */
	public TriggerState(String trigger, boolean state) {

		this.trigger = trigger;
		this.state = state;
	}
	
	/**
	 * @return the state
	 */
	public boolean getState() {
	
		return state;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(boolean state) {
	
		this.state = state;
	}
	
	/**
	 * @return the trigger
	 */
	public String getTrigger() {
	
		return trigger;
	}
}
