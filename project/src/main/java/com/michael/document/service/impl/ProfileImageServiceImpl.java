package com.michael.document.service.impl;

import com.michael.document.domain.User;
import com.michael.document.entity.ProfileImageEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exceptions.payload.ApiException;
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
import java.net.URL;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static org.springframework.http.MediaType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageServiceImpl implements ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;


    public static final String USER_IMAGE_PATH = "/user/image/";
    public static final String JPG_EXTENSION = "jpg";
    public static final String DOT = ".";
    public static final String FORWARD_SLASH = "/";
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";
    //   public static final String PATH_PREFIX = "/api/v1/";
    public static final String IMAGE_NOT_FOUND = "Profile image not found";

    //"https://cdn-icons-png.flaticon.com/512/149/149071.png"

    @Override
    public void saveTempProfileImage(UserEntity user) throws IOException {
        var filename = user.getUserId() + ".png";
        ProfileImageEntity profileImage = ProfileImageEntity.builder()
                .userEntity(user)
                .fileName(filename)
                .fileType(IMAGE_JPEG_VALUE)
                .data(compressImage(getDefaultImage(user.getUsername())))
                .profileImageURL(setProfileImageUrl(filename))
                .build();
        profileImage = profileImageRepository.save(profileImage);
        user.setProfileImageURL(profileImage.getProfileImageURL());
        userRepository.save(user);

        log.info("Saved Profile Image in database by name: {}", profileImage.getFileName());
    }

    @Override
    public byte[] getProfileImage(String fileName) {
        ProfileImageEntity image = profileImageRepository.findProfileImageEntityByFileName(fileName)
                .orElseThrow(() -> new ApiException(IMAGE_NOT_FOUND));
        return decompressImage(image.getData());
    }

    @Override
    public String updateProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new ApiException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            ProfileImageEntity profileImageEntityDB =
                    profileImageRepository.findProfileImageEntityByProfileImageURL(user.getProfileImageURL())
                            .orElseThrow(() -> new ApiException(IMAGE_NOT_FOUND));
            profileImageEntityDB.setData(compressImage(profileImage.getBytes()));
            profileImageEntityDB.setFileType(profileImage.getContentType());
            profileImageRepository.save(profileImageEntityDB);
            log.info("Saved new image profile in database by name: {}", profileImage.getOriginalFilename());
            return profileImageEntityDB.getProfileImageURL();
        } else {
            throw new ApiException(IMAGE_NOT_FOUND);
        }
    }


    @Override
    public String deleteProfileImageAndSetDefaultImage(User user) throws IOException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(user.getUserId())
                .orElseThrow(() -> new ApiException("User not found"));
        ProfileImageEntity profileImage = profileImageRepository.findProfileImageEntityByUserEntity(userEntity)
                .orElseThrow(() -> new ApiException(IMAGE_NOT_FOUND));
        profileImage.setData(compressImage(getDefaultImage(user.getUsername())));
        profileImage.setFileType(IMAGE_JPEG_VALUE);
        profileImageRepository.save(profileImage);
        return profileImage.getProfileImageURL();
    }

    private byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    private byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
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
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String setProfileImageUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + filename).toUriString();
    }

}
