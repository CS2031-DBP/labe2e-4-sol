package org.e2e.e2e.vehicle.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.e2e.e2e.driver.domain.Category;


@Entity
@Data
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Integer fabricationYear;

    @Column(nullable = false)
    private Integer capacity;

    public void setCategory(Category category) {
    }
}
