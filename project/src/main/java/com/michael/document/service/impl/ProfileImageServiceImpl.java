package com.michael.document.service.impl;

import com.michael.document.domain.User;
import com.michael.document.entity.ProfileImageEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.exceptions.payload.NotFoundException;
import com.michael.document.repository.ProfileImageRepository;
import com.michael.document.repository.UserRepository;
import com.michael.document.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static com.michael.document.constant.Constants.*;
import static com.michael.document.utils.FileCompressor.compressData;
import static com.michael.document.utils.FileCompressor.decompressData;
import static org.springframework.http.MediaType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageServiceImpl implements ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;

    @Override
    public void saveTempProfileImage(UserEntity user) throws IOException {
        var filename = user.getUserId() + PNG_EXTENSION;
        ProfileImageEntity profileImage = ProfileImageEntity.builder()
                .userEntity(user)
                .fileName(filename)
                .fileType(IMAGE_JPEG_VALUE)
                .data(compressData(getDefaultImage(user.getUsername())))
                .profileImageURL(setProfileImageUrl(filename))
                .build();
        profileImage = profileImageRepository.save(profileImage);
        user.setProfileImageURL(profileImage.getProfileImageURL());
        userRepository.save(user);
        log.info("Saved Profile Image in database by name: {}", profileImage.getFileName());
    }

    @Override
    public byte[] getProfileImage(String fileName) {
        return decompressData(findProfileImageInDB(fileName).getData());
    }

    private ProfileImageEntity findProfileImageInDB(String fileName) {
        return profileImageRepository.findProfileImageEntityByFileName(fileName)
                .orElseThrow(() -> new NotFoundException(String.format(NO_IMAGE_FOUND_BY_FILENAME, fileName)));
    }

    @Override
    public String updateProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new ApiException(String.format(NOT_AN_IMAGE_FILE, profileImage.getOriginalFilename()));
            }
            ProfileImageEntity profileImageEntityDB = findProfileImageByURL(user.getProfileImageURL());
            profileImageEntityDB.setData(compressData(profileImage.getBytes()));
            profileImageEntityDB.setFileType(profileImage.getContentType());
            profileImageRepository.save(profileImageEntityDB);
            log.info("Saved new image profile in database by name: {}", profileImage.getOriginalFilename());
            return profileImageEntityDB.getProfileImageURL();
        } else {
            throw new ApiException(IMAGE_NOT_FOUND);
        }
    }

    private ProfileImageEntity findProfileImageByURL(String url) {
        return profileImageRepository.findProfileImageEntityByProfileImageURL(url)
                .orElseThrow(() -> new NotFoundException(String.format(NO_IMAGE_FOUND_BY_URL, url)));
    }


    @Override
    public String deleteProfileImageAndSetDefaultImage(User user) throws IOException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(user.getUserId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));//todo: fix
        ProfileImageEntity profileImage = findProfileImageByUser(userEntity);
        profileImage.setData(compressData(getDefaultImage(user.getUsername())));
        profileImage.setFileType(IMAGE_JPEG_VALUE);
        profileImageRepository.save(profileImage);
        return profileImage.getProfileImageURL();
    }

    private ProfileImageEntity findProfileImageByUser(UserEntity userEntity) {
        return profileImageRepository.findProfileImageEntityByUserEntity(userEntity)
                .orElseThrow(() -> new NotFoundException(String.format(NO_IMAGE_FOUND_BY_USER, userEntity.getUsername())));
    }


    private byte[] getDefaultImage(String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }catch (MalformedURLException e) {
            // Обработка ошибки неверного URL
            e.printStackTrace();
            // Либо выбросить новое исключение, либо вернуть значение по умолчанию
            throw new ApiException("Invalid URL: " + TEMP_PROFILE_IMAGE_BASE_URL + username);
        } catch (IOException e) {
            // Обработка ошибки ввода/вывода
            e.printStackTrace();
            // Либо выбросить новое исключение, либо вернуть значение по умолчанию
            throw new ApiException("Failed to read from URL: " + TEMP_PROFILE_IMAGE_BASE_URL + username);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String setProfileImageUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + filename).toUriString();
    }
}
