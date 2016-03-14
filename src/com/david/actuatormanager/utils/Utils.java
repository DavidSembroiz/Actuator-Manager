package com.david.actuatormanager.utils;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.david.actuatormanager.Actuator;

public class Utils {
	
	private static Utils instance = new Utils();
	
	public static Utils getInstance() {
		return instance;
	}

	public String extractIdFromTopic(String topic) {
		return topic.split("/")[0];
	}

	public ArrayList<String> extractIdsFromActuators(ArrayList<Actuator> actuators) {
		ArrayList<String> ret = new ArrayList<String>();
		for (Actuator a : actuators) {
			ret.add(a.getSoid());
		}
		return ret;
	}

	public String getJsonAction(String mess) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject root = (JSONObject) parser.parse(mess);
			JSONObject description = (JSONObject) root.get("description");
			return (String) description.get("name");
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public String getJsonStatus(String mess) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject root = (JSONObject) parser.parse(mess);
			JSONObject description = (JSONObject) root.get("parameters");
			return (String) description.get("status");
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
