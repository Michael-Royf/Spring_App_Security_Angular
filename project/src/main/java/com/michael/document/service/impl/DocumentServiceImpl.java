package com.michael.document.service.impl;

import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.payload.response.DocumentResponse;
import com.michael.document.repository.DocumentRepository;
import com.michael.document.service.DocumentService;
import com.michael.document.service.UserService;
import com.michael.document.utils.DocumentUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.michael.document.utils.DocumentUtils.*;
import static com.michael.document.utils.FileCompressor.compressData;
import static com.michael.document.utils.FileCompressor.decompressData;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;

    public static final String NO_DOCUMENT_FOUND_BY_ID = "No document found by ID: %s";
    public static final String UNABLE_TO_SAVE_DOCUMENTS = "Unable to save documents";
    public static final String INVALID_FILE_NAME = "Invalid file name: %s";
    //  public static final String UNABLE_TO_UPDATE_DOCUMENTS = "Unable to update documents";
    final String DOCUMENT_RETRIEVAL_ERROR = "Unable to retrieve document with ID: %s";


    @Override
    public Collection<DocumentResponse> saveDocument(String userId, List<MultipartFile> documents) {
        List<DocumentResponse> listDocuments = new ArrayList<>();
        var userEntity = userService.getUserEntityByUserId(userId);
        try {
            for (MultipartFile document : documents) {
                var filename = cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if ("..".contains(filename)) {
                    throw new ApiException(String.format(INVALID_FILE_NAME, filename));
                }
                var documentEntity = DocumentEntity.builder()
                        .documentId(UUID.randomUUID().toString())
                        .name(filename)
                        .data(compressData(document.getBytes()))
                        .extension(getExtension(filename))
                        .uri(getDocumentUri(filename))
                        .formattedSize(byteCountToDisplaySize(document.getSize()))
                        .icon(setIcon(getExtension(filename)))
                        .owner(userEntity)
                        .feedbacks(Collections.EMPTY_LIST)
                        .totalLikes(0)
                        .likedUsers(Collections.EMPTY_SET)
                        .downloadCount(0L)
                        .build();
                DocumentEntity savedDocument = documentRepository.save(documentEntity);
                log.info("Saved document in database by name: {}", savedDocument.getName());
                listDocuments.add(convertDocumentEntityToResponse(documentEntity));
            }
            return listDocuments;
        } catch (Exception exception) {
            throw new ApiException(UNABLE_TO_SAVE_DOCUMENTS);
        }
    }


    @Override
    public Page<DocumentResponse> getAllDocuments(int pageNo, int pageSize,
                                                  String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<DocumentEntity> documentsEntity = documentRepository.findAll(pageable);

        List<DocumentResponse> documentResponses = documentsEntity
                .stream()
                .map(DocumentUtils::convertDocumentEntityToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(documentResponses,
                pageable,
                documentsEntity.getTotalElements());
    }

    @Override
    public Page<DocumentResponse> searchAllDocumentsByNameOrDescription(String query, int pageNo, int pageSize,
                                                                        String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);

        Page<DocumentEntity> documentsEntity = documentRepository.searchDocuments(query, pageable);
        List<DocumentResponse> documentResponses = documentsEntity
                .stream()
                .map(DocumentUtils::convertDocumentEntityToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(documentResponses,
                pageable,
                documentsEntity.getTotalElements());
    }

    @Override
    public Page<DocumentResponse> getAllUserDocument(String userId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        UserEntity userEntity = userService.getUserEntityByUserId(userId);
        Page<DocumentEntity> documentsEntity = documentRepository.findDocumentEntityByOwner(userEntity, pageable);

        List<DocumentResponse> documentResponses = documentsEntity
                .stream()
                .map(DocumentUtils::convertDocumentEntityToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(documentResponses,
                pageable,
                documentsEntity.getTotalElements());
    }

    @Override
    public DocumentResponse toggleLike(String username, String documentId) {
        var document = getDocumentEntity(documentId);
        Optional<String> userLiked = document.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username))
                .findAny();

        if (userLiked.isPresent()) {
            document.setTotalLikes(document.getTotalLikes() - 1);
            document.getLikedUsers().remove(username);
        } else {
            document.setTotalLikes(document.getTotalLikes() + 1);
            document.getLikedUsers().add(username);
        }
        document = documentRepository.save(document);
        return convertDocumentEntityToResponse(document);
    }


    @Override
    public DocumentResponse updateDocument(String userId, String documentId,
                                           String name, String description) {
        DocumentEntity documentEntity = getDocumentEntity(documentId);
        validateDocumentOwnerPermission(documentEntity, userId);
        documentEntity.setName(name);
        documentEntity.setDescription(description);
        documentRepository.save(documentEntity);
        return convertDocumentEntityToResponse(documentEntity);
    }

    @Override
    public void deleteDocument(String userId, String documentId) {
        DocumentEntity documentEntity = getDocumentEntity(documentId);
        validateDocumentOwnerPermission(documentEntity, userId);
        documentRepository.delete(documentEntity);
        log.info("Document with id {} was deleted", documentId);
    }


    @Override
    public DocumentResponse getDocumentResponseByDocumentId(String documentId) {
        return convertDocumentEntityToResponse(getDocumentEntity(documentId));
    }

    @Override
    public DocumentEntity getDocumentEntityByDocumentId(String documentId) {
        return getDocumentEntity(documentId);
    }

    private DocumentEntity getDocumentEntity(String documentId) {
        return documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ApiException(String.format(NO_DOCUMENT_FOUND_BY_ID, documentId)));
    }

    @Override
    public Resource getResource(String documentId) {
        var documentEntity = getDocumentEntity(documentId);
        try {
            ByteArrayResource byteArrayResource = new ByteArrayResource(decompressData(documentEntity.getData()));
            documentEntity.setDownloadCount(documentEntity.getDownloadCount() + 1);
            documentRepository.save(documentEntity);
            return byteArrayResource;
        } catch (Exception exception) {
            throw new ApiException(String.format(DOCUMENT_RETRIEVAL_ERROR, documentId));
        }
    }

    // Проверяем, является ли текущий пользователь владельцем документа
    private void validateDocumentOwnerPermission(DocumentEntity documentEntity, String userId) {
        if (!documentEntity.getOwner().getUserId().equals(userId)) {
            throw new ApiException("You do not have permission to delete or update this document");
        }
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }
}
