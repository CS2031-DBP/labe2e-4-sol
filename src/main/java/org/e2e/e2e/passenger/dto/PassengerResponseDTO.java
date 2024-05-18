package org.e2e.e2e.passenger.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PassengerResponseDTO {
    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;
    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;
    @NotNull
    @Size(min = 9, max = 12)
    private String phoneNumber;
    @NotNull
    @DecimalMax("5")
    @DecimalMin("0")
    private Float avgRating;
}
