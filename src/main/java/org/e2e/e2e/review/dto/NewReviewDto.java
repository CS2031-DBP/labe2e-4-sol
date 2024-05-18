package org.e2e.e2e.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewReviewDto {
    @NotNull
    @Size(min = 1, max = 255)
    private String comment;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    @NotNull
    private Long rideId;
    @NotNull
    private Long targetId;
}
