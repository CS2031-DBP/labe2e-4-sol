package org.e2e.e2e.review.application;

import org.e2e.e2e.review.domain.ReviewService;
import org.e2e.e2e.review.dto.NewReviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewReview (@RequestBody NewReviewDto newReview){
        reviewService.createNewReview(newReview);
        return ResponseEntity.ok("Review created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview (@PathVariable Long id){
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }


}
