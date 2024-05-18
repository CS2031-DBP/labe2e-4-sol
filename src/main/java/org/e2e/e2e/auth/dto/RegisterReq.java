package org.e2e.e2e.auth.dto;

import lombok.Data;
import org.e2e.e2e.driver.domain.Category;
import org.e2e.e2e.vehicle.dto.VehicleBasicDto;

@Data
public class RegisterReq {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Boolean isDriver=false;
    private Category category;
    private VehicleBasicDto vehicle;
}
