package org.e2e.e2e.vehicle.infrastructure;

import org.e2e.e2e.vehicle.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBrandInAndFabricationYearGreaterThanEqual(List<String> brands, Integer year);

    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
