package com.michael.document.payload.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedbackRequest {
    @DecimalMin(value = "0.0", inclusive = false, message = "Rating must be greater than 0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Maximum rating is 5")
    private Double documentRating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    @NotBlank(message = "Document ID cannot be empty")
    private String documentId;
}
