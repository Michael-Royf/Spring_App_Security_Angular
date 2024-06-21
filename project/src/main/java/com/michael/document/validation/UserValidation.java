package com.michael.document.validation;

import com.michael.document.entity.UserEntity;
import com.michael.document.exceptions.payload.ApiException;

public class UserValidation {

    public static void verifyAccountStatus(UserEntity userEntity) {
        if (!userEntity.isEnabled()) {
            throw new ApiException("User is disable");
        }
        if (!userEntity.isAccountNonExpired()) {
            throw new ApiException("Account is expired");
        }
        if (!userEntity.isAccountNonLocked()) {
            throw new ApiException("Account is locked");
        }
    }


}
