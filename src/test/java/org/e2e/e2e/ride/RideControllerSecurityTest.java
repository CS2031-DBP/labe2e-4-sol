package org.e2e.e2e.ride;

import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.coordinate.infrastructure.CoordinateRepository;
import org.e2e.e2e.driver.infrastructure.DriverRepository;
import org.e2e.e2e.exceptions.ResourceNotFoundException;
import org.e2e.e2e.passenger.domain.Passenger;
import org.e2e.e2e.passenger.infrastructure.PassengerRepository;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.ride.domain.Status;
import org.e2e.e2e.ride.infrastructure.RideRepository;
import org.e2e.e2e.utils.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
@SpringBootTest
@AutoConfigureMockMvc
public class RideControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    CoordinateRepository coordinateRepository;


    @BeforeEach
    public void setUp() throws Exception {
        rideRepository.deleteAll();
        coordinateRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();

        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(Reader.readJsonFile("/driver/post.json")));

        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(Reader.readJsonFile("/passenger/post.json")));
    }

    @Test
    @WithMockUser(roles = "PASSENGER", value = "janedoe@example.com")
    public void testAuthorizedRideRequestAndExpectOk() throws Exception {
        mockMvc.perform(post("/ride/request")
                .contentType(APPLICATION_JSON)
                .content(Reader.readJsonFile("/ride/post.json"))
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DRIVER", value = "johndoe@example.com")
    public void testAuthorizedAccessToAssignRide() throws Exception {

        Long rideId = createRide();

        mockMvc.perform(patch("/ride/assign/{rideId}", rideId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        assertEquals(Status.ACCEPTED, ride.getStatus());
        assertEquals("johndoe@example.com", ride.getDriver().getEmail());

    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToAssignRide() throws Exception {

        Long rideId = createRide();

        mockMvc.perform(patch("/ride/assign/{rideId}", rideId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"PASSENGER", "DRIVER"}, value = "janedoe@example.com")
    public void testAuthorizedAccessToCancelRide() throws Exception {
        Long rideId = createRide();
        mockMvc.perform(patch("/ride/{rideId}", rideId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToCancelRide() throws Exception {
        Long rideId = createRide();
        assignRide(rideId);
        mockMvc.perform(patch("/ride/{rideId}", rideId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PASSENGER", value = "janedoe@example.com")
    public void testAuthorizedAccessToGetRidesByUser() throws Exception {
        Long id = createRide();
        completeRide(id);
        mockMvc.perform(get("/ride/user?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToGetRidesByUser() throws Exception {
        Long id = createRide();
        completeRide(id);
        mockMvc.perform(get("/ride/user?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Long createRide() {
        Coordinate origin = new Coordinate();
        origin.setLatitude(37.775938);
        origin.setLongitude(-122.419664);

        Coordinate destination = new Coordinate();
        destination.setLatitude(37.775938);
        destination.setLongitude(-122.419664);

        Ride ride = new Ride();
        ride.setOriginName("Home");
        ride.setDestinationName("School");
        ride.setPrice(100d);
        ride.setOriginCoordinates(origin);
        ride.setDestinationCoordinates(destination);
        ride.setStatus(Status.valueOf("REQUESTED"));
        Passenger passenger = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
        ride.setPassenger(passenger);

        return rideRepository.save(ride).getId();
    }

    private void assignRide(Long rideId) {
        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        ride.setStatus(Status.ACCEPTED);

        rideRepository.save(ride);
    }

    private void completeRide(Long rideId) {
        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        ride.setStatus(Status.COMPLETED);

        rideRepository.save(ride);
    }
}
