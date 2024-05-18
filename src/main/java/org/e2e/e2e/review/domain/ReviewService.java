package org.e2e.e2e.review.domain;


import org.e2e.e2e.auth.utils.AuthorizationUtils;
import org.e2e.e2e.exceptions.ResourceNotFoundException;
import org.e2e.e2e.passenger.exceptions.UnauthorizeOperationException;
import org.e2e.e2e.review.dto.NewReviewDto;
import org.e2e.e2e.review.infrastructure.ReviewRepository;
import org.e2e.e2e.ride.domain.Ride;
import org.e2e.e2e.ride.infrastructure.RideRepository;
import org.e2e.e2e.user.domain.User;
import org.e2e.e2e.user.infrastructure.BaseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BaseUserRepository<User> userRepository;

    private final RideRepository rideRepository;

    @Autowired
    AuthorizationUtils authorizationUtils;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BaseUserRepository<User> userRepository, RideRepository rideRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
    }

    public void createNewReview(NewReviewDto newReview) {

        String authorEmail = authorizationUtils.getCurrentUserEmail();
        if (authorEmail == null) throw new UnauthorizeOperationException("Anonymous User not allowed to access this resource");

        Optional<? extends User> author = userRepository.findByEmail(authorEmail);
        Optional<? extends User> targetId = userRepository.findById(newReview.getTargetId());

        if (author.isEmpty()) throw new ResourceNotFoundException("Author not found");
        if (targetId.isEmpty()) throw new ResourceNotFoundException("Target not found");

        Ride ride = rideRepository
                .findById(newReview.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        Review review = new Review();
        review.setAuthor(author.get());
        review.setTarget(targetId.get());
        review.setRide(ride);
        review.setComment(newReview.getComment());
        review.setRating(newReview.getRating());


        reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        Review reviewToDelete = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check that the current user is the same as the owner of the resource
        if (!authorizationUtils.isAdminOrResourceOwner(reviewToDelete.getAuthor().getId()))
            throw new UnauthorizeOperationException("User has no permission to modify this resource");

        reviewRepository.delete(reviewToDelete);
    }
}
