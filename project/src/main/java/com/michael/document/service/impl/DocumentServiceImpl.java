package com.michael.document.service.impl;

import com.michael.document.domain.Document;
import com.michael.document.domain.api.IDocument;
import com.michael.document.entity.DocumentEntity;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.repository.DocumentRepository;
import com.michael.document.repository.UserRepository;
import com.michael.document.service.DocumentService;
import com.michael.document.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.michael.document.constant.Constants.FILE_STORAGE;
import static com.michael.document.utils.DocumentUtil.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;// TODO:
    private final UserService userService;

    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return documentRepository.findDocumentsByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Collection<Document> saveDocument(String userId, List<MultipartFile> documents) {
        List<Document> newDocuments = new ArrayList<>();
        var userEntity = userRepository.findUserEntityByUserId(userId).get();//todo:
        var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        log.info("paths {}", storage);
        try {
            for (MultipartFile document : documents) {
                var filename = cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if ("..".contains(filename)) {
                    throw new ApiException(String.format("Invalid file name: %s", filename));
                }
                var documentEntity = DocumentEntity.builder()
                        .documentId(UUID.randomUUID().toString())
                        .name(filename)
                        .owner(userEntity)
                        .extension(getExtension(filename))
                        .uri(getDocumentUri(filename))
                        .formattedSize(byteCountToDisplaySize(document.getSize()))
                        .icon(setIcon(getExtension(filename)))
                        .build();
                log.info("create document entity");
                DocumentEntity savedDocument = documentRepository.save(documentEntity);
                log.info("save document entity in db");
                Files.copy(document.getInputStream(), storage.resolve(filename), REPLACE_EXISTING);
                log.info("copy file in storage");
//                Document newDocument = new Document();
//                log.info("create document in from");
//                BeanUtils.copyProperties(documentEntity, document);
//                log.info("copy document  with BEANS UTILS");
//
//           Long idUpdatedBy = savedDocument.getUpdatedBy();
//                log.info("idUpdatedBy {}" ,idUpdatedBy);
//                User updatedBy = userService.getUserById(3L);
//                User createdBy = userService.getUserById(3L);
//                newDocument.setOwmerName(createdBy.getFirstName() + " " + createdBy.getLastName());
//                newDocument.setOwnerEmail(createdBy.getEmail());
//                newDocument.setOwnerPhone(createdBy.getPhone());
//                newDocument.setOwnerLastLogin(createdBy.getLastLogin());
//                newDocument.setUpdaterName(updatedBy.getFirstName() + " " + updatedBy.getLastName());

//                Document newDocument = fromDocumentEntity(savedDocument,
//                        userService.getUserById(savedDocument.getCreatedBy()),
//                        userService.getUserById(savedDocument.getUpdatedBy()));
                //   savedDocument.getOwner().getId();
                //TODO: check idm
                Document newDocument = fromDocumentEntity(savedDocument,
                        userService.getUserById(savedDocument.getOwner().getId()),
                        userService.getUserById(savedDocument.getOwner().getId()));

                log.info("create DOCUMENT");
                newDocuments.add(newDocument);
                log.info("add  DOCUMENT to list");
            }
            return newDocuments;
        } catch (Exception exception) {
            throw new ApiException("Unable to save documents");
        }

    }

    @Override
    public IDocument updateDocument(String documentId, String name, String description) {
        try {
            var documentEntity = getDocumentEntity(documentId);
            var document = Paths.get(FILE_STORAGE).resolve(documentEntity.getName()).toAbsolutePath().normalize();
            Files.move(document, document.resolveSibling(name), REPLACE_EXISTING);
            documentEntity.setName(name);
            documentEntity.setDescription(description);
            documentRepository.save(documentEntity);
            return getDocumentByDocumentId(documentId);
        } catch (Exception exception) {
            throw new ApiException("Unable to update documents");
        }
    }

    private DocumentEntity getDocumentEntity(String documentId) {
        return documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ApiException("Document Not found"));
    }

    @Override
    public void deleteDocument(String documentId) {

    }

    @Override
    public IDocument getDocumentByDocumentId(String documentId) {
        return documentRepository.findDocumentByDocumentId(documentId)
                .orElseThrow(() -> new ApiException("Document Not found"));
    }

    @Override
    public Resource getResource(String documentName) {
        try {
            var filePath = Paths.get(FILE_STORAGE).toAbsolutePath().normalize().resolve(documentName);
            if (!Files.exists(filePath)) {
                throw new ApiException("Document not found");
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception exception) {
            throw new ApiException("Unable to update documents");
        }
    }
}
