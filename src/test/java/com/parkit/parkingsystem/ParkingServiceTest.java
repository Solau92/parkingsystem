package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
//    private void setUpPerTest() {
    public void setUpPerTest() {
      
    	try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            LocalDateTime inTime = LocalDateTime.now().minusHours(1);
            ticket.setInTime(inTime);            
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            // Rajouté
            ticket.setFareRate(1.0);
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    	} catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    	
//    	try {
//            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
//            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
//            Ticket ticket = new Ticket();
//            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
//            ticket.setParkingSpot(parkingSpot);
//            ticket.setVehicleRegNumber("ABCDEF");
//            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
//            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
//            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
//            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw  new RuntimeException("Failed to set up test mock objects");
//        }
    }

    
    // Vérifie que méthode updateParking a été appelée une fois
    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    // Rajouté (sur modèle autre méthode)
    @Test
    public void processIncomingVehicleTest() {
//        parkingService.processIncomingVehicle();
//        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
//        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }
    
}
