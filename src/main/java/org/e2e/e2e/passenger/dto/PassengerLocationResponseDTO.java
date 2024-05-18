package org.e2e.e2e.passenger.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PassengerLocationResponseDTO {
    @NotNull
    private Long coordinateId;
    @NotNull
    @DecimalMax("90")
    @DecimalMin("-90")
    private Double latitude;
    @NotNull
    @DecimalMax("180")
    @DecimalMin("-180")
    private Double longitude;
    @NotNull
    @Size(min = 1, max = 255)
    private String description;
}
