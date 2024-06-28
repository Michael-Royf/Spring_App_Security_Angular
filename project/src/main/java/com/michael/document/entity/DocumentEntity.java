package com.michael.document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.michael.document.entity.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id"
//            ,
//            foreignKey = @ForeignKey(name = "fk_documents_owner",
//                    foreignKeyDefinition = "foreign key /* FK */ (user_id) references UserEntity",
//                    value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity owner;
}
