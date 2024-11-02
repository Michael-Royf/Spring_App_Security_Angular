package com.michael.document.repository;

import com.michael.document.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository  extends JpaRepository<FeedbackEntity, Long> {

    Optional<FeedbackEntity> findByFeedbackId(String feedbackId);

    Page<FeedbackEntity> findAllByDocumentId(Long documentId, Pageable pageable);
}
