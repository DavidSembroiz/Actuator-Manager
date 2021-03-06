package com.david.actuatormanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.david.actuatormanager.gui.MainGUI;

public class Controller {
	
	public Controller() {
		
	}
	
	private MainGUI gui;
	private Manager manager;

	public void reconnectToDatabase() {
		
		manager.reconnectToDatabase();
	}
	
	public void setManager(Manager m) {
		manager = m;
	}
	
	public void setGUI(MainGUI g) {
		gui = g;
	}

	public void reconnectToBroker() {
		manager.cleanSubscriptions();
		gui.appendOutputText(manager.reconnectToBroker());
	}

	public ArrayList<Room> getRoomTree() {
		return manager.getRoomTree();
	}
	
	public void unsubscribe(HashMap<String, Set<String>> acts) {
		manager.unsubscribe(acts);
	}


	public void subscribe(HashMap<String, Set<String>> acts) {
		manager.subscribe(acts);
	}

	public void appendOutputText(String s) {
		gui.appendOutputText(s);
	}

	public void updateView(Actuator a) {
		gui.updateView(a);
	}

	public void initialiseManager() {
		manager.initComponents();
	}

}
