package com.michael.document.service;

import com.michael.document.payload.request.FeedbackRequest;
import com.michael.document.payload.response.FeedbackResponse;
import org.springframework.data.domain.Page;

public interface FeedbackService {

    FeedbackResponse createFeedback(String userId, FeedbackRequest feedbackRequest);

    Page<FeedbackResponse> findAllFeedbacksByDocument(Long documentId, int pageNo, int pageSize, String sortBy, String sortDir);

    FeedbackResponse getFeedbackById(String feedbackId);

    FeedbackResponse updateFeedback(String feedbackId, String userId, FeedbackRequest feedbackRequest);

    void deleteFeedback(String feedbackId, String userId);
}
