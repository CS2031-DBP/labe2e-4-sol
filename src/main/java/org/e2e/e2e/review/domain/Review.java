package org.e2e.e2e.review.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.user.domain.User;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String comment;

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @OneToOne
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    private User target;
}
