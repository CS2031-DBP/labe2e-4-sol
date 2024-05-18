package org.e2e.e2e.vehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleBasicDto {
    @NotNull
    private String brand;
    @NotNull
    private String model;
    @NotNull
    @Size(min = 7, max = 7)
    private String licensePlate;
    @NotNull
    @Min(1900)
    @Max(2024)
    private Integer fabricationYear;
    @NotNull
    @Min(2)
    @Max(10)
    private Integer capacity;
}
