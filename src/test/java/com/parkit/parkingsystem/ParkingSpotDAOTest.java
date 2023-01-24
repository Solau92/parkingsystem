package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAO;

	@Mock
	private static Connection connection;
	@Mock
	private static DataBaseConfig dataBaseConfig;
	@Mock
	private static PreparedStatement preparedStatement;
	@Mock
	private static ResultSet resultSet;	
	@Mock
	private static Logger logger;

    
    @Test
    void getNextAvailableSlot_Ok_Test() {
    	fail("Not yet implemented");
    }
    
    @Test
    void getNextAvailableSlot_NoAvailable_Test() {
    	fail("Not yet implemented");
    }
    
    @Test
    void upDateParking_Ok_Test() {
    	fail("Not yet implemented");
    }
    
    @Test
    void upDateParking_Error_Test() {
    	fail("Not yet implemented");
    }
}
