package com.michael.document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.michael.document.entity.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "documents")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DocumentEntity extends Auditable {
    @Column(updatable = false, unique = true, nullable = false)
    private String documentId;
    //@Column(nullable = false, unique = true)
    private String name;
    @Lob
    @JdbcTypeCode(Types.BINARY)
    private byte[] data;
    private String description;
    private String uri;
    private long size;
    private String formattedSize;
    private String icon;
    private String extension; //расширение
    private Long downloadCount;
    private  int totalLikes;
    @Column(name = "liked_users")
    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id"
//            ,
//            foreignKey = @ForeignKey(name = "fk_documents_owner",
//                    foreignKeyDefinition = "foreign key /* FK */ (user_id) references UserEntity",
//                    value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity owner;

    @OneToMany(mappedBy = "document")
    List<FeedbackEntity> feedbacks;


    @Transactional
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks
                .stream()
                .mapToDouble(FeedbackEntity::getDocumentRating)
                .average()
                .orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }
}
