package com.michael.document.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedbackResponse {
    private String feedbackId;
    private String comment;
    private String ownerFullName;
    private LocalDateTime createdAt;
}
