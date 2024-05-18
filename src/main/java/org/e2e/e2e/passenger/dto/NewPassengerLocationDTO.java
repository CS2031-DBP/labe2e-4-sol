package org.e2e.e2e.passenger.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPassengerLocationDTO {
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    @NotNull
    private Double latitude;
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    @NotNull
    private Double longitude;
    @NotNull
    @Size(min = 1, max = 255)
    private String description;
}

