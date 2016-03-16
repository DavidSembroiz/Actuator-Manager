package com.david.actuatormanager.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.david.actuatormanager.Actuator;
import com.david.actuatormanager.Controller;
import com.david.actuatormanager.Room;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class MainGUI {

	private Display display;
	protected Shell shell;
	private Tree tree;
	private Controller controller;
	private Text text;
	
	public MainGUI(Controller c) {
		controller = c;
	}

	
	/**
	 * Open the window.
	 */
	public void open() {
	    display = Display.getDefault();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shell.dispose();
		System.exit(0);
	}

	
	private void createMenu() {
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem fileMenuHeader = new MenuItem(menu, SWT.CASCADE);
		fileMenuHeader.setText("File");
		
		Menu cascadeMenu = new Menu(fileMenuHeader);
		fileMenuHeader.setMenu(cascadeMenu);
		
		MenuItem databaseItem = new MenuItem(cascadeMenu, SWT.NONE);
		databaseItem.setText("Open database config");
		databaseItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File f = new File("database.properties.txt");
				try {
					if (System.getProperty("os.name").toLowerCase().contains("windows")) {
						  String cmd = "rundll32 url.dll,FileProtocolHandler " + f.getCanonicalPath();
						  Runtime.getRuntime().exec(cmd);
						  //controller.reconnectToDatabase();
						} 
						else {
						  java.awt.Desktop.getDesktop().edit(f);
						}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		
		MenuItem brokerItem = new MenuItem(cascadeMenu, SWT.NONE);
		brokerItem.setText("Open broker config");
		brokerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File f = new File("broker.properties.txt");
				try {
					if (System.getProperty("os.name").toLowerCase().contains("windows")) {
						  String cmd = "rundll32 url.dll,FileProtocolHandler " + f.getCanonicalPath();
						  Runtime.getRuntime().exec(cmd);
						  //controller.reconnectToBroker();
						} 
						else {
						  java.awt.Desktop.getDesktop().edit(f);
						}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	public void createContents() {
		shell = new Shell();
		shell.setSize(589, 523);
		shell.setText("Actuator Manager");
		
		createMenu();
		createTree();
		createSubscriptionButton();
		createLogArea();
		createReconnectButtons();
		updateRoomTree(controller.getRoomTree());
	}
	
	private void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    item.setChecked(checked);
	    TreeItem[] items = item.getItems();
	    for (int i = 0; i < items.length; i++) {
	        checkItems(items[i], checked);
	    }
	}
	
	private void createSubscriptionButton() {
		Button btnSubscribe = new Button(shell, SWT.NONE);
		btnSubscribe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				subscribeToSelectedActuators();
			}
		});
		btnSubscribe.setBounds(420, 85, 140, 25);
		btnSubscribe.setText("Subscribe");
		
		Button btnUnsubscribe = new Button(shell, SWT.NONE);
		btnUnsubscribe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				unsubscribeFromSelectedActuators();
			}
		});
		btnUnsubscribe.setText("Unsubscribe");
		btnUnsubscribe.setBounds(420, 116, 140, 25);
	}
	
	private void createReconnectButtons() {
		Button btnReconnectToBroker = new Button(shell, SWT.NONE);
		btnReconnectToBroker.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				controller.reconnectToBroker();
			}
		});
		btnReconnectToBroker.setBounds(420, 53, 140, 25);
		btnReconnectToBroker.setText("Reconnect to Broker");
		
		Button btnReconnectToDatabase = new Button(shell, SWT.NONE);
		btnReconnectToDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				controller.reconnectToDatabase();
			}
		});
		btnReconnectToDatabase.setText("Reconnect to Database");
		btnReconnectToDatabase.setBounds(420, 22, 140, 25);
		
	}
	
	private void createLogArea() {
		text = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setBounds(20, 284, 540, 171);
	}
	
	
	private void createTree() {
		tree = new Tree(shell, SWT.BORDER | SWT.CHECK);
		tree.setHeaderVisible(true);
		tree.addListener(SWT.Selection, event -> {
		    if (event.detail == SWT.CHECK) {
		        TreeItem item = (TreeItem) event.item;
		        checkItems(item, item.getChecked());
		    }
		});
		TreeColumn roomCol = new TreeColumn(tree, SWT.NONE);
		roomCol.setText("Room");
		roomCol.setWidth(180);
		roomCol.setAlignment(SWT.LEFT);
		
		TreeColumn lastActionCol = new TreeColumn(tree, SWT.NONE);
		lastActionCol.setText("Last Action");
		lastActionCol.setWidth(100);
		lastActionCol.setAlignment(SWT.LEFT);
		
		TreeColumn parameterCol = new TreeColumn(tree, SWT.NONE);
		parameterCol.setText("State");
		parameterCol.setWidth(100);
		parameterCol.setAlignment(SWT.LEFT);
		
		tree.setBounds(20, 22, 384, 244);	
	}

	public void updateRoomTree(ArrayList<Room> rooms) {
		boolean first = true;
		for (Room r : rooms) {
			TreeItem roomItem = new TreeItem(tree, SWT.NONE);
			roomItem.setText(r.getLocation());
			for (Actuator a : r.getActuators()) {
				TreeItem actuatorItem = new TreeItem(roomItem, SWT.CHECK);
				actuatorItem.setText(new String[] {a.getModel(), a.getLastAction(), a.getState()});
			}
			if (first) {
				roomItem.setExpanded(true);
				first = false;
			}
		}
	}
	
	private void unsubscribeFromSelectedActuators() {
		HashMap<String, Set<String>> acts = new HashMap<String, Set<String>>();
		for (TreeItem rloc : tree.getItems()) {
			Set<String> act = new HashSet<String>();
			for (TreeItem ract : rloc.getItems()) {
				if (ract.getChecked()) {
					act.add(ract.getText());
				}
			}
			if (act.size() > 0) acts.put(rloc.getText(), act);
		}
		
		controller.unsubscribe(acts);
	}
	
	private void subscribeToSelectedActuators() {
		HashMap<String, Set<String>> acts = new HashMap<String, Set<String>>();
		for (TreeItem rloc : tree.getItems()) {
			Set<String> act = new HashSet<String>();
			for (TreeItem ract : rloc.getItems()) {
				if (ract.getChecked()) {
					act.add(ract.getText());
				}
			}
			if (act.size() > 0) acts.put(rloc.getText(), act);
		}
		
		controller.subscribe(acts);
	}
	
	public void appendOutputText(String s) {
		display.syncExec(
		    new Runnable() {
		       public void run() {
			       text.append(s + "\n");
			   }
		    }
		);
	}


	public void initialise() {
		createContents();
		open();
	}


	public void updateView(Actuator a) {
		display.syncExec(
			    new Runnable() {
			       public void run() {
			    	   TreeItem actItem = findActuatorItem(a.getLocation(), a.getModel());
			    	   if (actItem != null) {
			    		   actItem.setText(1, a.getLastAction());
			    		   actItem.setText(2, a.getState());
			    	   }
			       }
			    }
			);
	}
	
	private TreeItem findActuatorItem(String location, String model) {
		TreeItem[] roots = tree.getItems();
		for (TreeItem ti : roots) {
			if (ti.getText().equals(location)) {
				TreeItem[] actuators = ti.getItems();
				for (TreeItem a : actuators) {
					if (a.getText().equals(model)) return a;
				}
			}
		}
		return null;
	}
}
