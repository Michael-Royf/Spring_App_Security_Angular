package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.payload.request.FeedbackRequest;
import com.michael.document.payload.response.FeedbackResponse;
import com.michael.document.payload.response.Response;
import com.michael.document.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.michael.document.constant.PaginationConstants.*;
import static com.michael.document.utils.ResponseUtils.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/create")
    public ResponseEntity<Response> createFeedback(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody FeedbackRequest feedbackRequest,
                                                   HttpServletRequest request) {
        FeedbackResponse newFeedback = feedbackService.createFeedback(user.getUserId(), feedbackRequest);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("feedback", newFeedback),
                "Feedback was created",
                HttpStatus.CREATED));
    }


    @GetMapping("/document_id/{documentId}")
    public ResponseEntity<Response> getAllFeedbacksByDocument(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @PathVariable("documentId") Long documentId,
            HttpServletRequest request
    ) {
        Page<FeedbackResponse> feedbacks = feedbackService.findAllFeedbacksByDocument(documentId, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("feedbacks", feedbacks),
                "Feedbacks retrieved",
                HttpStatus.OK));
    }


    @GetMapping("/{feedbackId}")
    public ResponseEntity<Response> getFeedbackById(@PathVariable("feedbackId") String feedbackId,
                                                    HttpServletRequest request) {
        var feedback = feedbackService.getFeedbackById(feedbackId);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("feedback", feedback),
                "Feedback retrieved",
                HttpStatus.OK));
    }

    @PatchMapping("/update/{feedbackId}")
    public ResponseEntity<Response> updateFeedback(@AuthenticationPrincipal User user,
                                                   @PathVariable String feedbackId,
                                                   @RequestBody @Valid FeedbackRequest feedbackRequest,
                                                   HttpServletRequest request) {
        var feedbackResponse = feedbackService.updateFeedback(feedbackId, user.getUserId(), feedbackRequest);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("feedback", feedbackResponse),
                "Feedback updated",
                HttpStatus.OK));
    }

    @DeleteMapping("delete/{feedbackId}")
    public ResponseEntity deleteDocument(@AuthenticationPrincipal User user,
                                         @PathVariable String feedbackId,
                                         HttpServletRequest request) {
        feedbackService.deleteFeedback(feedbackId, user.getUserId());
        return ResponseEntity.ok(getResponse(
                request,
                emptyMap(),
                "Feedback was deleted",
                HttpStatus.OK));
    }
}
