package com.michael.document.domain;

import com.michael.document.entity.RoleEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bio;
    private String profileImageURL;
    private String qrCodeImageUri;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String role;
    private String authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean mfa;
}
