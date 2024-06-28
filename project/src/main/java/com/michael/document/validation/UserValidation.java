package com.michael.document.validation;

import com.michael.document.entity.UserEntity;
import com.michael.document.exceptions.payload.ApiException;

public class UserValidation {
    public static final String USER_IS_DISABLE = "User is disable";
    public static final String ACCOUNT_IS_EXPIRED = "Account is expired";
    public static final String ACCOUNT_IS_LOCKED = "Account is locked";

    public static void verifyAccountStatus(UserEntity userEntity) {
        if (!userEntity.isEnabled()) {
            throw new ApiException(USER_IS_DISABLE);
        }
        if (!userEntity.isAccountNonExpired()) {
            throw new ApiException(ACCOUNT_IS_EXPIRED);
        }
        if (!userEntity.isAccountNonLocked()) {
            throw new ApiException(ACCOUNT_IS_LOCKED);
        }
    }
}
