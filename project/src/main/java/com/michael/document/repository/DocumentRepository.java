package com.michael.document.repository;

import com.michael.document.domain.api.IDocument;
import com.michael.document.entity.DocumentEntity;
import com.michael.document.payload.response.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static com.michael.document.constant.Constants.*;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

//    @Query(countQuery = SELECT_COUNT_DOCUMENTS_QUERY, value = SELECT_DOCUMENTS_QUERY, nativeQuery = true)
//    Page<IDocument> findDocuments(Pageable pageable);
//
//    @Query(countQuery = SELECT_COUNT_DOCUMENTS_BY_NAME_QUERY, value = SELECT_DOCUMENTS_BY_NAME_QUERY, nativeQuery = true)
//    Page<IDocument> findDocumentsByName(@Param("documentName") String documentName, Pageable pageable);

//    @Query(value = SELECT_DOCUMENT_QUERY, nativeQuery = true)
//        //TODO: don't work
//    Optional<IDocument> findDocumentByDocumentId(String documentId);

    Optional<DocumentEntity> findByDocumentId(String documentId);

     @Query("SELECT d FROM DocumentEntity d WHERE d.name LIKE CONCAT('%', :query, '%') OR d.description LIKE CONCAT('%', :query, '%')")
    //@Query("SELECT d FROM DocumentEntity d WHERE d.name LIKE CONCAT('%', :query, '%')")
     List<DocumentEntity> searchDocuments(String query);
}
