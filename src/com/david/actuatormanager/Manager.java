package com.david.actuatormanager;


import java.util.HashMap;
import com.david.actuatormanager.utils.Database;
import com.david.actuatormanager.utils.Mqtt;
import com.david.actuatormanager.utils.Utils;

public class Manager {
	
	private static final int QUERY_ALL = 99999999;
	
	private Database db;
	private Utils uts;
	private HashMap<String, Actuator> actuators = new HashMap<String, Actuator>();
	
	public Manager() {
		db = Database.getInstance();
		uts = Utils.getInstance();
		actuators = new HashMap<String, Actuator>();
		actuators = db.queryActuators(QUERY_ALL);
		
		new Mqtt(this, actuators.keySet());
		
	}

	public void manageMessage(String topic, String mess) {
		
		String soid = uts.extractIdFromTopic(topic);
		System.out.println(soid);
		String action = uts.getJsonAction(mess);
		String status = uts.getJsonStatus(mess);
		
		Actuator a = actuators.get(soid);
		
		a.setLastAction(action);
		a.setState(status);
		
		System.out.println("SOID " + soid + ". Action " + action + " done with parameter " + status);
	}

}
