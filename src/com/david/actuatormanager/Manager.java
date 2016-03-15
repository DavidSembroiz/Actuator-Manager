package com.david.actuatormanager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.david.actuatormanager.utils.Database;
import com.david.actuatormanager.utils.Mqtt;
import com.david.actuatormanager.utils.Utils;

public class Manager {
	
	private static final int QUERY_ALL = 99999999;
	
	private Database db;
	private Utils uts;
	private HashMap<String, Actuator> actuators = new HashMap<String, Actuator>();
	private ArrayList<Room> rooms;
	private Mqtt mqtt;
	private Controller controller;
	
	public Manager(Controller c) {
		controller = c;
		db = Database.getInstance();
		uts = Utils.getInstance();
		actuators = new HashMap<String, Actuator>();
		actuators = db.queryActuators(QUERY_ALL);
		
		mqtt = new Mqtt(this, actuators.keySet());
		
	}

	public void manageMessage(String topic, String mess) {
		
		String soid = uts.extractIdFromTopic(topic);
		String action = uts.getJsonAction(mess);
		String status = uts.getJsonStatus(mess);
		
		Actuator a = actuators.get(soid);
		
		a.setLastAction(action);
		a.setState(status);
		
		controller.appendOutputText("SOID " + soid + ". Action " + action + " done with parameter " + status);
		
	}
	
	private boolean roomExists(ArrayList<Room> rooms, String loc) {
		for (Room r : rooms) {
			if (r.getLocation().equals(loc)) return true;
		}
		return false;
	}
	
	private Room findRoom(ArrayList<Room> rooms, String loc) {
		for (Room r : rooms) {
			if (r.getLocation().equals(loc)) return r;
		}
		return null;
	}

	public ArrayList<Room> getRoomTree() {
		rooms = new ArrayList<Room>();
		Iterator it = actuators.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Actuator a = (Actuator) pair.getValue();
			if (!roomExists(rooms, a.getLocation())) {
				rooms.add(new Room(a.getLocation(), a));
			}
			else {
				Room r = findRoom(rooms, a.getLocation());
				if (r != null) {
					r.addActuator(a);
				}
			}
		}
		Collections.sort(rooms, new Comparator<Room>() {
			@Override
			public int compare(Room r1, Room r2) {
				return r1.getLocation().compareTo(r2.getLocation());
			}
		});
		
		for (Room r : rooms) {
			Collections.sort(r.getActuators(), new Comparator<Actuator>() {
				@Override
				public int compare(Actuator a1, Actuator a2) {
					return a1.getModel().compareTo(a2.getModel());
				}
			});
		}
		
		return rooms;
	}

	public void subscribe(HashMap<String, Set<String>> acts) {
		Iterator it = acts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			for (Room r : rooms) {
				if (r.getLocation().equals(pair.getKey())) {
					subscribeToList((Set<String>) pair.getValue(), r.getActuators());
				}
			}
		}
	}

	private void subscribeToList(Set<String> actsList, ArrayList<Actuator> acts) {
		for (String a : actsList) {
			for (Actuator ac : acts) {
				if (a.equals(ac.getModel()) && !ac.isSubscribed()) {
					controller.appendOutputText(mqtt.subscribe(ac.getSoid()));
					ac.setSubscribed(true);
				}
			}
		}
	}

	public void reconnectToDatabase() {
		db.reconnect();
	}

	public void reconnectToBroker() {
		mqtt.reconnect();
	}
}
