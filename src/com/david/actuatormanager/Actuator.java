package com.david.actuatormanager;

public class Actuator {
	
	private String soid;
	private String model;
	private String location;
	private String state;
	private int stateValue;
	private String lastAction;
	
	public Actuator(String soid, String model, String location) {
		this.soid = soid;
		this.model = model;
		this.location = location;
	}
	

	public String getSoid() {
		return soid;
	}

	public void setSoid(String soid) {
		this.soid = soid;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public void setStateValue(int value) {
		stateValue = value;
	}
	
	public String getState() {
		return state;
	}
	
	public int getStateValue() {
		return stateValue;
	}


	public String getLastAction() {
		return lastAction;
	}


	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
}
