package org.e2e.e2e.ride.infrastructure;

import org.e2e.e2e.passenger.domain.Passenger;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.ride.domain.Status;
import org.e2e.e2e.ride.dto.RidesByUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Page<Ride> findAllByPassengerIdAndStatus(Long passenger_id, Status status, Pageable pageable);
}

