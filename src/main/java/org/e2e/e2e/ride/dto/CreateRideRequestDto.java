package org.e2e.e2e.ride.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.e2e.e2e.coordinate.domain.Coordinate;

@Data
public class CreateRideRequestDto {
    private String originName;
    private String destinationName;
    @NotNull
    @PositiveOrZero
    private Double price;
    @NotNull
    private Coordinate originCoordinates;
    @NotNull
    private Coordinate destinationCoordinates;
}
