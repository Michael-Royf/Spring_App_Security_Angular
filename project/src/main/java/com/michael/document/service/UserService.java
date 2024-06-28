package com.michael.document.service;

import com.michael.document.domain.User;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.enumerations.LoginType;
import com.michael.document.payload.request.*;

import java.io.IOException;

public interface UserService {
    void createUser(RegistrationRequest request) throws IOException;

    void saveUser(UserEntity userEntity);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    UserEntity getUserEntityByUserId(String userId);

    User getUserById(Long id);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long userId);

    User setUpMfa(Long id);

    User cancelMfa(Long id);

    User verifyQrCode(String userId, String qrCode);

    User verifyPasswordKey(String key);

    void resetPassword(EmailRequest emailRequest);

    void updatePassword(ResetPasswordRequest resetPasswordRequest);

    void updatePassword(String userId, UpdatePasswordRequest updatePasswordRequest);

    User updateUser(String userId, RegistrationRequest registrationRequest);

    void updateRole(String userId, RoleRequest roleRequest);

    //
    void toggleAccountExpired(String userId);

    void toggleAccountLocked(String userId);

    void toggleAccountEnabled(String userId);

    void toggleCredentialsExpired(String userId);


}
