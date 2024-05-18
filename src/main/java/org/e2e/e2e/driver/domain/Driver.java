package org.e2e.e2e.driver.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.user.domain.User;
import org.e2e.e2e.vehicle.domain.Vehicle;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Driver extends User {

    @Column(nullable = false)
    private Category category;

    @Column
    private String hexAdress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "driver")
    private List<Ride> rides;

}
