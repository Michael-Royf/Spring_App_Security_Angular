package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.payload.request.UpdateDocument;
import com.michael.document.payload.response.DocumentResponse;
import com.michael.document.payload.response.Response;
import com.michael.document.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.michael.document.constant.PaginationConstants.*;
import static com.michael.document.utils.ResponseUtils.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Response> saveDocuments(@AuthenticationPrincipal User user,
                                                  @RequestParam("files") List<MultipartFile> documents,
                                                  HttpServletRequest request) {
        var newDocuments = documentService.saveDocument(user.getUserId(), documents);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(
                        request,
                        Map.of("documents", newDocuments),
                        "Document(s) uploaded",
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<Response> getDocuments(@RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                 @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
                                                 @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir,
                                                 HttpServletRequest request) {
        var documents = documentService.getAllDocuments(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("documents", documents),
                "Documents retrieved",
                HttpStatus.OK));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getAllUserDocumentByUserId(@PathVariable("userId") String userId,
                                                               @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                               @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                               @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
                                                               @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir,
                                                               HttpServletRequest request) {
        var documents = documentService.getAllUserDocument(userId, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("documents", documents),
                "Documents retrieved",
                HttpStatus.OK));
    }


    @GetMapping("/search/{query}")
    public ResponseEntity<Response> searchDocuments(@PathVariable("query") String query,
                                                    @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                    @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
                                                    @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir,
                                                    HttpServletRequest request) {
        var documents = documentService.searchAllDocumentsByNameOrDescription(query, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("documents", documents),
                "Document(s) retrieved",
                HttpStatus.OK));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Response> getDocumentById(@PathVariable("documentId") String documentId,
                                                    HttpServletRequest request) {
        var document = documentService.getDocumentResponseByDocumentId(documentId);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("document", document),
                "Document retrieved",
                HttpStatus.OK));
    }


    @PatchMapping("/update")
    public ResponseEntity<Response> updateDocument(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid UpdateDocument updateDocument,
                                                   HttpServletRequest request) {
        var document = documentService.updateDocument(
                user.getUserId(),
                updateDocument.getDocumentId(),
                updateDocument.getName(),
                updateDocument.getDescription());
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("document", document),
                "Document updated",
                HttpStatus.OK));
    }

    @Transactional(readOnly = true)
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") String documentId,
                                                     HttpServletRequest request) throws IOException {
        var resource = documentService.getResource(documentId);
        var httpHeader = new HttpHeaders();
        httpHeader.add("File-Name", documentId);
        httpHeader.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;File-Name=%s", resource.getFilename()));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentId + "\"")
                .body(resource);
    }

    @DeleteMapping("delete/{documentId}")
    public ResponseEntity deleteDocument(@AuthenticationPrincipal User user,
                                         @PathVariable String documentId,
                                         HttpServletRequest request) {
        documentService.deleteDocument(user.getUserId(), documentId);
        return ResponseEntity.ok(getResponse(
                request,
                emptyMap(),
                "Document was deleted",
                HttpStatus.OK));
    }


    @PatchMapping("/toggle_like/{document_id}")
    public ResponseEntity<Response> toggleLike(@AuthenticationPrincipal User user,
                                               @PathVariable("document_id") String documentId,
                                               HttpServletRequest request) {
        DocumentResponse documentResponse = documentService.toggleLike(user.getUsername(), documentId);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("document", documentResponse),
                        "Updated successfully.",
                        HttpStatus.OK));
    }
}
