package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains main method to launch the app
 *
 */
public class App {
	
	private static final Logger logger = LogManager.getLogger("App");

	public static void main(String args[]) {
		logger.info("Initializing Parking System"); 
		InteractiveShell.loadInterface();
	}
}
