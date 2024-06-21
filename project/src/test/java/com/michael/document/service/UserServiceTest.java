package com.michael.document.service;

import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.enumerations.Authority;
import com.michael.document.repository.CredentialRepository;
import com.michael.document.repository.UserRepository;
import com.michael.document.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CredentialRepository credentialRepository;


    @InjectMocks
    private UserServiceImpl userService;


    @DisplayName("Test find user by ID")
    @Test
    public void getUserByIdTest() {
        //given - precondition or setup
        var userEntity = new UserEntity();
        userEntity.setFirstName("Michael");
        userEntity.setId(1L);
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1 , 1, 1 , 21));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1 , 1, 1 , 21));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1 , 1, 1 , 21));

        var roleEntity = new RoleEntity("USER", Authority.USER);
        userEntity.setRoles(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1 , 1, 1 , 21));
        credentialEntity.setPassword("password");
        credentialEntity.setUserEntity(userEntity);
        // when -action or the behavior we are going to test
        when(userRepository.findUserEntityByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialEntityByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));
        var userByUserId = userService.getUserByUserId("1");
        //then - verify the output

      assertThat(userByUserId.getFirstName()).isEqualTo(userEntity.getFirstName());

    }


}
