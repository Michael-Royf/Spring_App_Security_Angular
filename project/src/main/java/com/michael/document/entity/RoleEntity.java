package com.michael.document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.michael.document.entity.base.Auditable;
import com.michael.document.enumerations.Authority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoleEntity extends Auditable {
    @Column(nullable = false, unique = true)
    private String name;
    private Authority authority;
}
