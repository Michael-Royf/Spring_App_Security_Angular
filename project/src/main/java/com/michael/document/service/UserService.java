package com.michael.document.service;

import com.michael.document.domain.User;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.enumerations.LoginType;
import com.michael.document.payload.request.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void createUser(RegistrationRequest request) throws IOException;

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long userId);

    User setUpMfa(Long id);

    User cancelMfa(Long id);

    User verifyQrCode(String userId, String qrCode);

    void resetPassword(EmailRequest emailRequest);

    User verifyPasswordKey(String key);

    void updatePassword(ResetPasswordRequest resetPasswordRequest);

    void updatePassword(String userId, UpdatePasswordRequest updatePasswordRequest);

    User updateUser(String userId, RegistrationRequest registrationRequest);

    void updateRole(String userId, RoleRequest roleRequest);

    //
    void toggleAccountExpired(String userId);

    void toggleAccountLocked(String userId);

    void toggleAccountEnabled(String userId);

    void toggleCredentialsExpired(String userId);

    //
    String uploadPhoto(String userId, MultipartFile file);

    User getUserById(Long id);
}
