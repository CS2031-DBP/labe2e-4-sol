package org.e2e.e2e.vehicle.domain;

import org.e2e.e2e.exceptions.ResourceNotFoundException;
import org.e2e.e2e.vehicle.infrastructure.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void saveVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicle(Long id) {
        return vehicleRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }


}
