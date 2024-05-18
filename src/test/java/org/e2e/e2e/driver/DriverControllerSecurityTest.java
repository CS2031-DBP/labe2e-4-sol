package org.e2e.e2e.driver;

import org.e2e.e2e.driver.domain.Driver;
import org.e2e.e2e.driver.infrastructure.DriverRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
@WithMockUser(roles = "DRIVER")
@SpringBootTest
@AutoConfigureMockMvc
public class DriverControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    Reader reader;

    String token = "";

    private void createUnauthorizedDriver() throws Exception {
        String jsonContent = Reader.readJsonFile("/driver/post.json");
        jsonContent = reader.updateDriverEmail(jsonContent, "email", "other@example.com");
        jsonContent = reader.updateDriverEmail(jsonContent, "phone", "123-456-2220");
        jsonContent = reader.updateVehicleLicensePlate(jsonContent, "licensePlate", "ABC123222");

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andReturn();
    }

    @BeforeEach
    public void setUp() throws Exception {

        driverRepository.deleteAll();

        String jsonContent = Reader.readJsonFile("/driver/post.json");

        var res = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andReturn();

        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(res.getResponse().getContentAsString()));
        token = jsonObject.getString("token");
        System.out.println("Token: " + token);

    }

    @Test
    public void testAuthorizedAccessToGetDriverById() throws Exception {
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/driver/{driverId}", authorizedDriverId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PASSENGER")
    public void testPassengerAccessToGetDriverById() throws Exception {
        mockMvc.perform(get("/driver/{driverId}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "other@example.com", roles = "DRIVER")
    public void testUnauthorizedAccessToDeleteDriverById() throws Exception {
        createUnauthorizedDriver();
        Driver authorizedDriver = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow();

        mockMvc.perform(delete("/driver/{driverId}", authorizedDriver.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToGetOwnDriverInfo() throws Exception {
        mockMvc.perform(get("/driver/me")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testUnauthorizedAccessToGetOwnDriverInfo() throws Exception {
        mockMvc.perform(get("/driver/me")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "other@example.com", roles = "DRIVER")
    public void testUnauthorizedAccessToDeleteDriver() throws Exception {
        createUnauthorizedDriver();
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/driver/{driverId}", authorizedDriverId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToUpdateDriverInfo() throws Exception {
        String jsonContent = Reader.readJsonFile("/driver/patch.json");
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(patch("/driver/{driverId}", authorizedDriverId)
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(value = "other@example.com", roles = "DRIVER")
    public void testUnauthorizedAccessToUpdateDriverInfo() throws Exception {
        createUnauthorizedDriver();
        String jsonContent = Reader.readJsonFile("/driver/patch.json");
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(patch("/driver/{driverId}", authorizedDriverId)
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccessToUpdateDriverCar() throws Exception {
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        String jsonContent = Reader.readJsonFile("/vehicle/post.json");

        mockMvc.perform(patch("/driver/{driverId}/car", authorizedDriverId)
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(value = "other@example.com", roles = "DRIVER")
    public void testUnauthorizedAccessToUpdateDriverCar() throws Exception {
        createUnauthorizedDriver();
        Long authorizedDriverId = driverRepository
                .findByEmail("johndoe@example.com")
                .orElseThrow()
                .getId();

        String jsonContent = Reader.readJsonFile("/vehicle/post.json");

        mockMvc.perform(patch("/driver/{driverId}/car", authorizedDriverId)
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }
}
