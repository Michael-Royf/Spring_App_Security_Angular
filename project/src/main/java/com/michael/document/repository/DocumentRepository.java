package com.michael.document.repository;


import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    Optional<DocumentEntity> findByDocumentId(String documentId);

    @Query("SELECT d FROM DocumentEntity d WHERE d.name LIKE CONCAT('%', :query, '%') OR d.description LIKE CONCAT('%', :query, '%')")
    Page<DocumentEntity> searchDocuments(String query, Pageable pageable);

    Page<DocumentEntity> findDocumentEntityByOwner(UserEntity userEntity, Pageable pageable);
}
