package com.michael.document.repository;

import com.michael.document.entity.ProfileImageEntity;
import com.michael.document.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImageEntity, Long> {
    Optional<ProfileImageEntity> findProfileImageEntityByUserEntity(UserEntity user);

    Optional<ProfileImageEntity> findProfileImageEntityByFileName(String filename);

    Optional<ProfileImageEntity> findProfileImageEntityByProfileImageURL(String profileImageURL);
}
