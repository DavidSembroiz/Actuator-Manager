package com.david.actuatormanager.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.*;

import com.david.actuatormanager.Manager;

/**
 * 
 * @author David
 *
 */


public class Mqtt {
	
	private String ADDRESS;
	private String USERNAME;
	private String PASSWORD;
	private String CLIENTID;
	private MqttConnectOptions connOpts;
	private MqttClient client;
	private MqttCb callback;
	private Set<String> ids;
	private String topic;
	private Utils uts;
	private Properties prop;
	private Manager manager;

	public Mqtt(Manager m) {
		uts = Utils.getInstance();
		this.manager = m;
		loadProperties();
		connect();
	}
	
	/**
	 * Reads all the needed properties for the MQTT connection
	 */
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("broker.properties.txt");
			prop.load(is);
			ADDRESS = prop.getProperty("so_address");
			USERNAME = prop.getProperty("so_username");
			PASSWORD = prop.getProperty("so_password");
			CLIENTID = prop.getProperty("so_clientid");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to the ServIoTicy MQTT endpoint
	 * 
	 */
	public String connect() {
		connOpts = new MqttConnectOptions();
		
		/**
		 * Session has to be set to TRUE
		 */
		
		connOpts.setCleanSession(true);
		connOpts.setUserName(USERNAME);
		connOpts.setPassword(PASSWORD.toCharArray());
		//connOpts.setKeepAliveInterval(600);
		try {
			client = new MqttClient(ADDRESS, CLIENTID);
			callback = new MqttCb(manager);
			client.setCallback(callback);
			client.connect(connOpts);
			if (client.isConnected()) return "Connected to MQTT Broker";
		} catch (MqttException e) {
			if (client.isConnected()) return "Already connected";
		}
		return "Unable to connect to MQTT Broker";
	}
	
	
	/*public void subscribe(Set<String> ids) {
		for (String id : ids) subscribe(id);
	}*/
	
	public String subscribe(String id, String model, String location) {
		topic = id + "/actions";
		try {
			client.subscribe(topic);
			return "Subscribed to actuator " + model + " from " + location;
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return "Unable to subscribe to actuator " + model + " from " + location;
	}
	
	public String unsubscribe(String id, String model, String location) {
		topic = id + "/actions";
		try {
			client.unsubscribe(topic);
			return "Unsubscribed from actuator " + model + " from " + location;
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return "Unable to unsubscribe from actuator " + model + " from " + location;
	}
	
	
	public Set<String> getIds() {
		return ids;
	}
	
	public void addId(String id) {
		ids.add(id);
	}
	
	/**
	 * Disconnects the client from the ServIoTicy endpoint
	 * 
	 */
	public void disconnect() {
		try {
			client.disconnect();
			client.close();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public String reconnect() {
		disconnect();
		while (client.isConnected());
		loadProperties();
		return connect();
	}
}
