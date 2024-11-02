package com.michael.document.utils;

import com.michael.document.entity.FeedbackEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.payload.response.FeedbackResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FeedbackUtils {
    public static FeedbackResponse mapFeedbackEntityToFeedbackResponse(FeedbackEntity feedbackEntity) {

        return FeedbackResponse.builder()
                .feedbackId(feedbackEntity.getFeedbackId())
                .comment(feedbackEntity.getComment())
                .ownerFullName(feedbackEntity.getOwnerFullName())
                .createdAt(feedbackEntity.getCreatedAt())
                .build();
    }

    public static List<FeedbackResponse> mappingListFeedbackEntityToFeedbackResponse(List<FeedbackEntity> feedbackEntities) {
        return feedbackEntities.stream()
                .map(FeedbackUtils::mapFeedbackEntityToFeedbackResponse)
                .collect(Collectors.toList());
    }
}
