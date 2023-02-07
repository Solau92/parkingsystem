package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Contains methods to proceed to incoming and exiting vehicles
 */
public class ParkingService {

//	private static final Logger logger = LogManager.getLogger("ParkingService");
	private static Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * Sets the logger.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Calls method processIncomingVehicle(LocalDateTime) with parameter
	 * LocalDateTime = null
	 */
	public void processIncomingVehicle() {
		processIncomingVehicle(null);
	}

	/**
	 * If a parking spot is available and the vehicle is not already in parking,
	 * updates the status of the parking spot chosen in data base, and creates and
	 * saves the ticket in date base.
	 * 
	 * @param LocalDateTime inTime
	 * @throws Exception if vehicle already in parking, no spot available, and if
	 *                   problem updating parking spot in database
	 */
	public void processIncomingVehicle(LocalDateTime inTime) {
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();

			// If a parkingSpot is available
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehicleRegNumber();

				// If vehicle not already in parking
				if (!isVehicleAlreadyInParking(vehicleRegNumber)) {
					parkingSpot.setAvailable(false);

					if (!parkingSpotDAO.updateParking(parkingSpot)) {
						throw new Exception("Failed to update parking in database");
					}

					if (inTime == null) {
						inTime = LocalDateTime.now();
					}

					Ticket ticket = new Ticket();
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(0);
					ticket.setInTime(inTime);
					ticket.setOutTime(null);

					// If vehicle already parked in this parking before
					if (numberOfTimesVehicleAlreadyParkedInParking(vehicleRegNumber) > 0) {
						System.out.println(
								"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
						ticket.setFareRate(0.95);
					} else {
						// First time for this vehicle in this parking
						ticket.setFareRate(1.00);
					}

					ticketDAO.saveTicket(ticket);
					System.out.println("Generated Ticket and saved in DB");
					System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
					System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);

				} else {
					// Vehicle already in parking
					throw new Exception("Unable to process incoming vehicle : vehicle already in parking");
				}
			} else {
				// No parkingSpot available
				throw new Exception("No slot available");
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	/**
	 * Returns the vehicle regNumber.
	 * 
	 * @return the vehicle regNumer written by the user
	 * @throws Exception
	 */
	private String getVehicleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * Returns true or false whether the vehicle is or not in the parking
	 * 
	 * @param vehicleRegNumber
	 * @return true if the vehicle is already in parking, false if it is not (never
	 *         been there, or already out)
	 */
	private boolean isVehicleAlreadyInParking(String vehicleRegNumber) {
		return ticketDAO.isVehicleAlreadyInParkingInDataBase(vehicleRegNumber);
	}

	/**
	 * Returns the number of times the vehicle parked in parking before
	 * 
	 * @param vehicleRegNumber
	 * @return the number of times the vehicle parked in parking (0 if never)
	 */
	private int numberOfTimesVehicleAlreadyParkedInParking(String vehicleRegNumber) {
		return ticketDAO.numberOfTimesVehicleInDataBase(vehicleRegNumber);
	}

	/**
	 * Returns the parking spot chosen if one is available.
	 * 
	 * @return a parking spot
	 * @throws Exception                if no parking spot is available
	 * @throws IllegalArgumentException if problem of input
	 */
	private ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehicleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSpot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	/**
	 * Returns the parking type.
	 * 
	 * @return the parking type
	 * @throws IllegalArgumentException if the input is incorrect
	 */
	private ParkingType getVehicleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	/**
	 * Calls method processExitingVehicle(LocalDateTime) with parameter
	 * LocalDateTime = null
	 */
	public void processExitingVehicle() {
		processExitingVehicle(null);
	}

	/**
	 * Gets the ticket in data base and updates it (adding out time), calculates the
	 * fare and updates the ticket (adding fare), and updates the parking spot in
	 * data base (available).
	 * 
	 * @param outTime
	 * @throws Exception if error (in particular when vehicle not in the parking or
	 *                   error while searching ticket in database)
	 */
	public void processExitingVehicle(LocalDateTime outTime) {
		try {
			String vehicleRegNumber = getVehicleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

			if (Objects.nonNull(ticket)) {
				if (outTime == null) {
					outTime = LocalDateTime.now();
				}
				ticket.setOutTime(outTime);
				fareCalculatorService.calculateFare(ticket);

				if (ticketDAO.updateTicket(ticket)) {
					ParkingSpot parkingSpot = ticket.getParkingSpot();
					parkingSpot.setAvailable(true);
					parkingSpotDAO.updateParking(parkingSpot);
					System.out.println("Please pay the parking fare:" + ticket.getPrice());
					System.out.println(
							"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
				} else {
					throw new Exception("Unable to update ticket information. Error occurred");
				}
			} else {
				throw new Exception("Ticket is null : vehicle not in the parking");
			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}

}
