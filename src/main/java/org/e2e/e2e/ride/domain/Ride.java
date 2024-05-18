package org.e2e.e2e.ride.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.e2e.e2e.coordinate.domain.Coordinate;
import org.e2e.e2e.driver.domain.Driver;
import org.e2e.e2e.passenger.domain.Passenger;

import java.time.LocalDateTime;

@Entity
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originName;

    @Column(nullable = false)
    private String destinationName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Status status;

    @Column
    private LocalDateTime departureDate;

    @Column
    private LocalDateTime arrivalDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Coordinate originCoordinates;

    @OneToOne(cascade = CascadeType.ALL)
    private Coordinate destinationCoordinates;

    @ManyToOne
    private Driver driver;

    @ManyToOne
    private Passenger passenger;

}
