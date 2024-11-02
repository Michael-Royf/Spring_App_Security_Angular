package com.michael.document.service.impl;

import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.FeedbackEntity;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.exceptions.payload.NotFoundException;
import com.michael.document.payload.request.FeedbackRequest;
import com.michael.document.payload.response.FeedbackResponse;
import com.michael.document.repository.FeedbackRepository;
import com.michael.document.service.DocumentService;
import com.michael.document.service.FeedbackService;
import com.michael.document.service.UserService;
import com.michael.document.utils.FeedbackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.michael.document.utils.FeedbackUtils.mapFeedbackEntityToFeedbackResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final DocumentService documentService;
    private final UserService userService;


    @Override
    @Transactional
    public FeedbackResponse createFeedback(String userId, FeedbackRequest feedbackRequest) {
        var userEntity = userService.getUserEntityByUserId(userId);
        DocumentEntity documentEntity = documentService.getDocumentEntityByDocumentId(feedbackRequest.getDocumentId());

        FeedbackEntity feedback = FeedbackEntity.builder()
                .feedbackId(UUID.randomUUID().toString())
                .documentRating(feedbackRequest.getDocumentRating())
                .comment(feedbackRequest.getComment())
                .document(documentEntity)
                .owner(userEntity)
                .ownerFullName(userEntity.getFullName())
                .build();
        feedbackRepository.saveAndFlush(feedback);
        return mapFeedbackEntityToFeedbackResponse(feedback);
    }

    @Override
    public Page<FeedbackResponse> findAllFeedbacksByDocument(Long documentId, int pageNo,
                                                             int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<FeedbackEntity> feedbackEntityPage = feedbackRepository.findAllByDocumentId(documentId, pageable);
        List<FeedbackResponse> feedbackResponseList = feedbackEntityPage
                .stream()
                .map(FeedbackUtils::mapFeedbackEntityToFeedbackResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(feedbackResponseList,
                pageable,
                feedbackEntityPage.getTotalElements());
    }


    @Override
    public FeedbackResponse getFeedbackById(String feedbackId) {
        return mapFeedbackEntityToFeedbackResponse(findFeedbackById(feedbackId));
    }

    @Override
    public FeedbackResponse updateFeedback(String feedbackId, String userId, FeedbackRequest feedbackRequest) {
        FeedbackEntity feedbackEntity = findFeedbackById(feedbackId);
        validateFeedbackOwnerPermission(feedbackEntity, userId);
        feedbackEntity.setComment(feedbackRequest.getComment());
        feedbackEntity.setDocumentRating(feedbackRequest.getDocumentRating());
        feedbackRepository.save(feedbackEntity);
        return mapFeedbackEntityToFeedbackResponse(feedbackEntity);
    }

    @Override
    public void deleteFeedback(String feedbackId, String userId) {
        FeedbackEntity feedbackEntity = findFeedbackById(feedbackId);
        validateFeedbackOwnerPermission(feedbackEntity, userId);
        feedbackRepository.delete(feedbackEntity);
    }


    private FeedbackEntity findFeedbackById(String feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found"));
    }

    private void validateFeedbackOwnerPermission(FeedbackEntity feedbackEntity, String userId) {
        if (!feedbackEntity.getOwner().getUserId().equals(userId)) {
            throw new ApiException("You do not have permission to delete or update this feedback");
        }
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

}
