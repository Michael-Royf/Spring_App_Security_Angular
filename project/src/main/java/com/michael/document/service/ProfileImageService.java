package com.michael.document.service;

import com.michael.document.domain.User;
import com.michael.document.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileImageService {
    void saveTempProfileImage(UserEntity user) throws IOException;

    byte[] getProfileImage(String fileName);

    String updateProfileImage(User user, MultipartFile profileImage) throws IOException;

    String deleteProfileImageAndSetDefaultImage(User user) throws IOException;
}
