package org.e2e.e2e.ride.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RidesByUserDto {
    private String originName;
    private String destinationName;
    @NotNull
    @PositiveOrZero
    private Double price;
    private LocalDateTime departureDate;
}
