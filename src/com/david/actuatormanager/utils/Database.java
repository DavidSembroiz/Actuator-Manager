package com.david.actuatormanager.utils;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.postgresql.ds.PGPoolingDataSource;

import com.david.actuatormanager.Actuator;

public class Database {
	
	private static Database instance = new Database();
	
	private Database() {
		initComponents();
	}
	
	public static Database getInstance() {
		return instance;
	}
	
	private String DB_USERNAME;
	private String DB_PASSWORD;
	private String DB;
	private String DB_NAME;
	private String DB_TABLE;
	
	private PreparedStatement pst;
	
	private Properties prop;
	private PGPoolingDataSource poolSource;
	
	
	private void initComponents() {
		loadProperties();
		loadPoolSource();
	}
	
	public Connection getConnectionListener() {
		Connection c = null;
		try {
			c = poolSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("database.properties");
			prop.load(is);
			DB_USERNAME = prop.getProperty("db_username");
			DB_PASSWORD = prop.getProperty("db_password");
			DB = prop.getProperty("db");
			DB_NAME = prop.getProperty("db_name");
			DB_TABLE = prop.getProperty("db_table");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPoolSource() {
		poolSource = new PGPoolingDataSource();
		poolSource.setDataSourceName("DB Data Source");
		poolSource.setServerName(DB);
		poolSource.setDatabaseName(DB_NAME);
		poolSource.setUser(DB_USERNAME);
		poolSource.setPassword(DB_PASSWORD);
		poolSource.setMaxConnections(50);
	}
	
	private void closeConnection(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	public ArrayList<String> queryIds(int n) {
		ArrayList<String> ret = new ArrayList<String>();
		Connection c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT servioticy_id FROM " + DB_TABLE + " ORDER BY created DESC LIMIT ?");
			pst.setInt(1, n);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				ret.add(rs.getString("servioticy_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return ret;
	}
	
	public String getModel(String soID) {
		Connection c = null;
		String res = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT model FROM " + DB_TABLE + " WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) res = rs.getString("model");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return res;
	}
	
	public String getLocation(String soID) {
		Connection c = null;
		String res = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT location FROM " + DB_TABLE + " WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) res = rs.getString("location");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return res;
	}


	public HashMap<String, Actuator> queryActuators(int n) {
		HashMap<String, Actuator> ret = new HashMap<String, Actuator>();
		Connection c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT * FROM " + DB_TABLE + " ORDER BY created DESC LIMIT ?");
			pst.setInt(1, n);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				String id = rs.getString("servioticy_id");
				String model = rs.getString("model");
				String location = rs.getString("location");
				Actuator a = new Actuator(id, model, location);
				ret.put(id, a);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
}
