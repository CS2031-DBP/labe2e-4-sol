package org.e2e.e2e.user_locations.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class PassengerCoordinateId implements Serializable {

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "coordinate_id")
    private Long coordinateId;

    public PassengerCoordinateId() {}

    public PassengerCoordinateId(Long passengerId, Long coordinateId) {
        this.passengerId = passengerId;
        this.coordinateId = coordinateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PassengerCoordinateId that = (PassengerCoordinateId) o;

        return Objects.equals(passengerId, that.passengerId) &&
                Objects.equals(coordinateId, that.coordinateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerId, coordinateId);
    }
}
