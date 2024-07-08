package com.michael.document.service;

import com.michael.document.payload.response.DocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface DocumentService {

    Collection<DocumentResponse> saveDocument(String userId, List<MultipartFile> documents);

    DocumentResponse updateDocument(String userId,String documentId, String name, String description);

    void deleteDocument(String userId, String documentId);

    DocumentResponse getDocumentResponseByDocumentId(String documentId);

    Resource getResource(String documentId);

    Page<DocumentResponse> getAllDocuments(int pageNo, int pageSize, String sortBy, String sortDir);

    Page<DocumentResponse> searchAllDocumentsByNameOrDescription(String query, int pageNo, int pageSize, String sortBy, String sortDir);

    Page<DocumentResponse>  getAllUserDocument(String userId, int pageNo, int pageSize, String sortBy, String sortDir);



}
