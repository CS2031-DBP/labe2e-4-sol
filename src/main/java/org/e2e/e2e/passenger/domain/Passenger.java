package org.e2e.e2e.passenger.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.user.domain.User;
import org.e2e.e2e.user_locations.domain.UserLocation;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Passenger extends User {

    @OneToMany(mappedBy = "passenger",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UserLocation> coordinates = new ArrayList<>();

    @OneToMany(mappedBy = "passenger")
    private List<Ride> rides = new ArrayList<>();

    public void addCoordinate(Coordinate coordinate, String description) {
        UserLocation userLocation = new UserLocation(this, coordinate, description);
        coordinates.add(userLocation);
        coordinate.getPassengers().add(userLocation);
    }

    public void removeCoordinate(Coordinate coordinate) {
        for (UserLocation userLocation : coordinates) {
            if (userLocation.getPassenger().equals(this) && userLocation.getCoordinate().equals(coordinate)) {
                coordinates.remove(userLocation);
                userLocation.getCoordinate().getPassengers().remove(userLocation);
                userLocation.setPassenger(null);
                userLocation.setCoordinate(null);
                break;
            }
        }
    }
}
