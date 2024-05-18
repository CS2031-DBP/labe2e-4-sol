package org.e2e.e2e.passenger;

import org.e2e.e2e.coordinate.infrastructure.CoordinateRepository;
import org.e2e.e2e.passenger.infrastructure.PassengerRepository;
import org.e2e.e2e.utils.Reader;
import org.json.JSONObject;
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

import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "janedoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
@WithMockUser(roles = "PASSENGER")
@SpringBootTest
@AutoConfigureMockMvc
public class PassengerControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private CoordinateRepository coordinateRepository;

    @Autowired
    Reader reader;

    String token;

    private void createUnauthorizedPassenger() throws Exception {
        String jsonContent = Reader.readJsonFile("/passenger/post.json");
        jsonContent = reader.updateDriverEmail(jsonContent, "email", "other@example.com");
        jsonContent = reader.updateDriverEmail(jsonContent, "phone", "123-456-2220");

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andReturn();
    }

    @BeforeEach
    public void setUp() throws Exception {
        passengerRepository.deleteAll();

        String jsonContent = Reader.readJsonFile("/passenger/post.json");

        var res = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andReturn();

        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(res.getResponse().getContentAsString()));
        token = jsonObject.getString("token");
    }

    @Test
    public void testAuthorizedAccessToGetPassengerOwnInfo() throws Exception {
        mockMvc.perform(get("/passenger/me")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToGetPassengerOwnInfo() throws Exception {
        mockMvc.perform(get("/passenger/me")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToGetPassengerInfo() throws Exception {
        Long authorizedPassengerId = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/passenger/{passengerId}", authorizedPassengerId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthenticatedAccessToGetPassengerInfo() throws Exception {

        Long authorizedPassengerId = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/passenger/{passengerId}", authorizedPassengerId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToDeletePassenger() throws Exception {
        Long authorizedPassengerId = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/passenger/{id}", authorizedPassengerId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(value = "other@example.com", roles = "PASSENGER")
    public void testUnauthorizedAccessToDeletePassenger() throws Exception {
        createUnauthorizedPassenger();
        Long authorizedPassengerId = passengerRepository
                .findByEmail("janedoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/passenger/{passengerId}", authorizedPassengerId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToAddPassengerPlace() throws Exception {
        String jsonContent = Reader.readJsonFile("/passenger/postPlace.json");

        mockMvc.perform(post("/passenger/places")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthenticatedAccessToAddPassengerPlace() throws Exception {

        mockMvc.perform(post("/passenger/places")
                        .contentType(APPLICATION_JSON)
                        .content(Reader.readJsonFile("/passenger/postPlace.json")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToGetPassengerPlaces() throws Exception {


        mockMvc.perform(post("/passenger/places")
                        .contentType(APPLICATION_JSON)
                        .content(Reader.readJsonFile("/passenger/postPlace.json")))
                .andReturn();

        mockMvc.perform(get("/passenger/places")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Home"));
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToGetPassengerPlaces() throws Exception {
        mockMvc.perform(post("/passenger/places")
                        .contentType(APPLICATION_JSON)
                        .content(Reader.readJsonFile("/passenger/postPlace.json")))
                .andReturn();

        mockMvc.perform(get("/passenger/places")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToDeletePassengerPlace() throws Exception {
        Long coordinateId = coordinateRepository
                .findByLatitudeAndLongitude(40.01, 30.02)
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/passenger/places/{coordinateId}", coordinateId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToDeletePassengerPlace() throws Exception {
        Long coordinateId = coordinateRepository
                .findByLatitudeAndLongitude(40.01, 30.02)
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/passenger/places/{coordinateId}", coordinateId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
