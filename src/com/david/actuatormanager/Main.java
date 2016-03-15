package com.david.actuatormanager;

import com.david.actuatormanager.gui.MainGUI;

public class Main {
	
	public static void main(String[] args) {
		
		Controller c = new Controller();
		MainGUI app = new MainGUI(c);
		Manager m = new Manager(c);
		c.setGUI(app);
		c.setManager(m);

		app.initialise();
	}
}
