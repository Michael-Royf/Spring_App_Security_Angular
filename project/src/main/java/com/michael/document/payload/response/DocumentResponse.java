package com.michael.document.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentResponse {
    private Long id;
    private String documentId;
    private String name;
    private String description;
    private String uri;
    private long size;
    private String formattedSize;
    private String icon;
    private String extension;
    private String referenceId;
    private Double documentRating;
    private Long downloadCount;
    private int totalLikes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private LocalDateTime ownerLastLogin;
    private List<FeedbackResponse> feedbackResponses;
    //  private String updaterName;
}
