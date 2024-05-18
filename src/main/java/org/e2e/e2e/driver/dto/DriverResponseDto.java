package org.e2e.e2e.driver.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.e2e.e2e.driver.domain.Category;
import org.e2e.e2e.vehicle.dto.VehicleBasicDto;

@Data
public class DriverResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private Category category;
    @NotNull
    @Size(min = 2, max = 50)
    private String firstName;
    @NotNull
    @Size(min = 2, max = 50)
    private String lastName;
    @NotNull
    @PositiveOrZero
    private Integer trips;
    @DecimalMin("0.0")
    private Float avgRating;
    @NotNull
    @Valid
    private VehicleBasicDto vehicle;
}
