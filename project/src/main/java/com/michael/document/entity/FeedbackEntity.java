package com.michael.document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.michael.document.entity.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "feedbacks")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FeedbackEntity extends Auditable {
    @Column(updatable = false, unique = true, nullable = false)
    private String feedbackId;

    private Double documentRating;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity owner;

    @Column(name = "owner_fill_name", nullable = false)
    private String ownerFullName;
}
