package org.e2e.e2e.coordinate.infrastructure;

import org.e2e.e2e.coordinate.domain.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoordinateRepository extends JpaRepository<Coordinate, Long>{
    Optional<Coordinate> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
