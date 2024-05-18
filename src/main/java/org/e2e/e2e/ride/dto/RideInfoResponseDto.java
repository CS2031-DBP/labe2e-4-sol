package org.e2e.e2e.ride.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.driver.dto.DriverResponseDto;
import org.e2e.e2e.passenger.dto.PassengerResponseDTO;

@Data
public class RideInfoResponseDto {
    @NotNull
    private Long rideId;
    @NotNull
    @Size(min = 1, max = 50)
    private String originName;
    @NotNull
    @Size(min = 1, max = 50)
    private String destinationName;
    @NotNull
    @DecimalMax("90.0")
    @DecimalMin("-90.0")
    private Coordinate originCoordinates;
    @NotNull
    @DecimalMax("180.0")
    @DecimalMin("-180.0")
    private Coordinate destinationCoordinates;
    @NotNull
    private String status;
    @NotNull
    @Valid
    private PassengerResponseDTO passenger;
    @NotNull
    @Valid
    private DriverResponseDto driver;

}
