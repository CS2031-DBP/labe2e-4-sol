package org.e2e.e2e.review;

import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.driver.domain.Driver;
import org.e2e.e2e.driver.infrastructure.DriverRepository;
import org.e2e.e2e.exceptions.ResourceNotFoundException;
import org.e2e.e2e.passenger.domain.Passenger;
import org.e2e.e2e.passenger.infrastructure.PassengerRepository;
import org.e2e.e2e.review.domain.Review;
import org.e2e.e2e.review.infrastructure.ReviewRepository;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithUserDetails(value = "janedoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
@WithMockUser(roles = "PASSENGER")
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    Reader reader;

    @BeforeEach
    public void setUp() throws Exception {
        reviewRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();

        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(Reader.readJsonFile("/passenger/post.json")));

        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(Reader.readJsonFile("/driver/post.json")));

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

        rideRepository.save(ride);
    }


    @Test
    public void testAuthorizedAccessToCreateReview() throws Exception {
        Long rideId = rideRepository.findAll().get(0).getId();
        Long passengerId = passengerRepository.findAll().get(0).getId();

        String jsonContent = Reader.readJsonFile("/review/post.json");
        jsonContent = reader.updateReviewRelatioshipsId(jsonContent, "rideId", rideId);
        jsonContent = reader.updateReviewRelatioshipsId(jsonContent, "targetId", passengerId);

        mockMvc.perform(post("/review/new")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToCreateReview() throws Exception {
        Long rideId = rideRepository.findAll().get(0).getId();
        Long targetId = driverRepository.findAll().get(0).getId();

        String jsonContent = Reader.readJsonFile("/review/post.json");
        jsonContent = reader.updateReviewRelatioshipsId(jsonContent, "rideId", rideId);
        jsonContent = reader.updateReviewRelatioshipsId(jsonContent, "targetId", targetId);

        mockMvc.perform(post("/review/new")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToDeleteReview() throws Exception {
        Long reviewId = createReview();

        mockMvc.perform(delete("/review/{reviewId}", reviewId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DRIVER", value = "johndoe@example.com")
    public void testUnauthorizedAccessToDeleteReview() throws Exception {
        Long reviewId = createReview();
        mockMvc.perform(delete("/review/{reviewId}", reviewId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Long createReview() throws Exception {

        Review review = new Review();
        Passenger author = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
        review.setAuthor(author);

        Driver target = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        review.setTarget(target);
        Ride ride = rideRepository
                .findAll()
                .get(0);
        review.setRide(ride);
        review.setComment("Good ride");
        review.setRating(5);


        return reviewRepository.save(review).getId();
    }

}
