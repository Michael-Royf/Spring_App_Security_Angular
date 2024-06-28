package com.michael.document.service.impl;

import com.michael.document.cache.CacheStore;
import com.michael.document.domain.User;
import com.michael.document.entity.ConfirmationEntity;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.entity.base.RequestContext;
import com.michael.document.enumerations.Authority;
import com.michael.document.enumerations.EventType;
import com.michael.document.enumerations.LoginType;
import com.michael.document.event.UserEvent;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.exceptions.payload.ExistException;
import com.michael.document.exceptions.payload.NotFoundException;
import com.michael.document.payload.request.*;
import com.michael.document.repository.ConfirmationRepository;
import com.michael.document.repository.CredentialRepository;
import com.michael.document.repository.RoleRepository;
import com.michael.document.repository.UserRepository;
import com.michael.document.service.ProfileImageService;
import com.michael.document.service.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.michael.document.constant.Constants.*;
import static com.michael.document.utils.UserUtils.*;
import static com.michael.document.validation.UserValidation.verifyAccountStatus;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final ApplicationEventPublisher publisher;
    private final CacheStore<String, Integer> userCache;
    private final PasswordEncoder encoder;
    private final ProfileImageService profileImageService;


    @Override
    public void createUser(RegistrationRequest request) throws IOException {
        validateNewUsernameAndEmail(
                StringUtils.EMPTY,
                request.getUsername(),
                request.getEmail());

        var userEntity = createNewUser(request);
        userRepository.save(userEntity);
        profileImageService.saveTempProfileImage(userEntity);
        var credentialEntity = new CredentialEntity(encoder.encode(request.getPassword()), userEntity);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public void saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public RoleEntity getRoleName(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseThrow(() ->
                new NotFoundException(String.format(NO_ROLE_FOUND_BY_NAME, name)));
    }

    @Override
    public void verifyAccountKey(String key) {
        ConfirmationEntity confirmationEntity = getUserConfirmation(key);
        UserEntity userEntity = getUserEntityByUsername(confirmationEntity.getUserEntity().getUsername());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findUserEntityByUserId(userId)
                .orElseThrow(() -> new NotFoundException(NO_USER_FOUND_BY_ID));
    }


    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }


    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        return credentialRepository.getCredentialEntityByUserEntityId(userId)
                .orElseThrow(() -> new NotFoundException(USER_CREDENTIAL_NOT_FOUND));
    }

    @Override
    public User setUpMfa(Long id) {
        var userEntity = getUserEntityById(id);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }


    @Override
    public User cancelMfa(Long id) {
        var userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setQrCodeImageUri(EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        var userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyPasswordKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        if (confirmationEntity == null) {
            throw new ApiException(TOKEN_NOT_FOUND);
        }
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        if (userEntity == null) {
            throw new ApiException(INCORRECT_TOKEN);
        }
        verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    //если забыл пароль
    @Override
    public void resetPassword(EmailRequest emailRequest) {
        var userEntity = getUserEntityByEmail(emailRequest.getEmail());
        var confirmation = getUserConfirmation(userEntity);
        if (confirmation != null) {
            publisher.publishEvent(new UserEvent(userEntity, EventType.RESET_PASSWORD, Map.of("key", confirmation.getKey())));
        } else {
            var confirmationEntity = new ConfirmationEntity(userEntity);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(userEntity, EventType.RESET_PASSWORD, Map.of("key", confirmationEntity.getKey())));
        }
    }

    @Override
    public void updatePassword(ResetPasswordRequest resetPasswordRequest) {
        var user = getUserByUserId(resetPasswordRequest.getUserId());
        var credentials = getUserCredentialById(user.getId());
        credentials.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
        credentialRepository.save(credentials);
    }

    @Override
    public void updatePassword(String userId, UpdatePasswordRequest updatePasswordRequest) {
        var user = getUserEntityByUserId(userId);
        verifyAccountStatus(user);
        var credentials = getUserCredentialById(user.getId());
        if (!encoder.matches(updatePasswordRequest.getCurrentPassword(), credentials.getPassword())) {
            throw new ApiException(EXISTING_PASSWORD_INCORRECT);
        }
        credentials.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
        credentialRepository.save(credentials);
    }

    @Override
    public User updateUser(String userId, RegistrationRequest registrationRequest) {
        var userEntity = getUserEntityByUserId(userId);
        validateNewUsernameAndEmail(
                userEntity.getUsername(),
                registrationRequest.getUsername(),
                registrationRequest.getEmail());

        userEntity.setFirstName(registrationRequest.getFirstName());
        userEntity.setLastName(registrationRequest.getLastName());
        userEntity.setUsername(registrationRequest.getUsername());
        userEntity.setEmail(registrationRequest.getEmail());
        userEntity.setBio(registrationRequest.getBio());
        userEntity.setPhone(registrationRequest.getPhone());
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updateRole(String userId, RoleRequest roleRequest) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setRoles(getRoleName(roleRequest.getRole()));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        var credentials = getUserCredentialById(userEntity.getId());
        credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
//        if (credentials.getUpdatedAt().plusDays(90).isAfter(LocalDateTime.now())) {
//            credentials.setUpdatedAt(LocalDateTime.now());
//        } else {
//            credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
//        }
        userRepository.save(userEntity);
    }


    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }


    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            log.error(INVALID_QR_CODE);
            throw new ApiException(INVALID_QR_CODE);
        }
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException(CONFIRMATION_INFORMATION_NOT_FOUND));
    }


    private ConfirmationEntity getUserConfirmation(UserEntity userEntity) {
        return confirmationRepository.findByUserEntity(userEntity)
                .orElse(null);
    }


    private UserEntity createNewUser(RegistrationRequest request) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), role);
    }

    private UserEntity createUserEntity(String username, String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .firstName(firstLetterUpper(firstName))
                .lastName(firstLetterUpper(lastName))
                .email(email)
                .roles(role)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)//TODO: fix
                .bio(EMPTY)
                //   .imageUrl(profileImageService.)
                // .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .build();
    }


    private void validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
        UserEntity userByNewUsername = findOptionalUserByUsername(newUsername).orElse(null);
        UserEntity userByNewEmail = findOptionalUserByEmail(newEmail).orElse(null);
        if (StringUtils.isNotBlank(currentUsername)) {
            UserEntity currentUser = findOptionalUserByUsername(currentUsername).orElse(null);
            if (currentUser == null) {
                throw new NotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, currentUsername));
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new ExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new ExistException(EMAIL_ALREADY_EXISTS);
            }
        } else {
            if (userByNewUsername != null) {
                throw new ExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new ExistException(EMAIL_ALREADY_EXISTS);
            }
        }
    }

    private Optional<UserEntity> findOptionalUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private UserEntity getUserEntityByEmail(String email) {
        return findOptionalUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format(NO_USER_FOUND_BY_EMAIL, email)));
    }

    private Optional<UserEntity> findOptionalUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private UserEntity getUserEntityByUsername(String username) {
        return findOptionalUserByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, username)));
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_USER_FOUND_BY_ID));
    }


    private String firstLetterUpper(String world) {
        String[] words = world.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String capitalizedWord = firstChar + word.substring(1).toLowerCase();
                result.append(capitalizedWord).append(" ");
            }
        }
        return result.toString().trim();
    }


}
