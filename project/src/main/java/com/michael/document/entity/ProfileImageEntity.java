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
@Table(name = "profile_images")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProfileImageEntity extends Auditable {

    @Column(nullable = false, unique = true, name = "file_name")
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    private byte[] data;

    @Column(name = "profile_image_URL", unique = true)
    private String profileImageURL;

    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}
