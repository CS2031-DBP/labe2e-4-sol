package org.e2e.e2e.user_locations.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.passenger.domain.Passenger;

@Entity
@Data
@EqualsAndHashCode
public class UserLocation {
    @EmbeddedId
    private PassengerCoordinateId id;

    public UserLocation(Passenger passenger, Coordinate coordinate, String description) {
        this.passenger =  passenger;
        this.coordinate = coordinate;
        this.description = description;
        this.id = new PassengerCoordinateId(passenger.getId(), coordinate.getId());
    }

    public UserLocation() {}

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("passengerId")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("coordinateId")
    private Coordinate coordinate;

}
