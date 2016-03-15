package com.david.actuatormanager;

import java.util.ArrayList;

public class Room {

	private String location;
	private ArrayList<Actuator> actuators;
	
	public Room(String loc, ArrayList<Actuator> acts) {
		location = loc;
		actuators = acts;
	}
	
	public Room(String loc, Actuator a) {
		location = loc;
		actuators = new ArrayList<Actuator>();
		actuators.add(a);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ArrayList<Actuator> getActuators() {
		return actuators;
	}

	public void setActuators(ArrayList<Actuator> actuators) {
		this.actuators = actuators;
	}
	
	public void addActuator(Actuator a) {
		actuators.add(a);
	}
	
	
}
