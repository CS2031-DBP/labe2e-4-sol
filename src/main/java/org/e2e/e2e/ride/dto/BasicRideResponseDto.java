package org.e2e.e2e.ride.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BasicRideResponseDto {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String originName;
    @NotNull
    @Size(min = 1, max = 100)
    private String destinationName;
    private String hexAddress;
    @NotNull
    private String status;
}
