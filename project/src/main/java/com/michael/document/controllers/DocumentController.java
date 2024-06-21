package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.payload.request.UpdateDocument;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.michael.document.utils.RequestUtils.getResponse;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;


    @PostMapping("/upload")
    public ResponseEntity<Response> saveDocument(@AuthenticationPrincipal User user,
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
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal User user,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                                 HttpServletRequest request) {
        var documents = documentService.getDocuments(page, size);

        return ResponseEntity.ok(getResponse(
                request,
                Map.of("documents", documents),
                "Documents retrieved",
                HttpStatus.OK));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchDocuments(@AuthenticationPrincipal User user,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @RequestParam(value = "name", defaultValue = "5") String name,
                                                    HttpServletRequest request) {
        var documents = documentService.getDocuments(page, size, name);

        return ResponseEntity.ok(getResponse(
                request,
                Map.of("documents", documents),
                "Document(s) retrieved",
                HttpStatus.OK));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Response> getDocumentById(@AuthenticationPrincipal User user,
                                                    @PathVariable("documentId") String documentId,
                                                    HttpServletRequest request) {

        var document = documentService.getDocumentByDocumentId(documentId);
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("document", document),
                "Document retrieved",
                HttpStatus.OK));
    }


    @PatchMapping
    public ResponseEntity<Response> updatedocument(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid UpdateDocument updateDocument,
                                                   HttpServletRequest request) {
        var document = documentService.updateDocument(
                updateDocument.getDocumentId(),
                updateDocument.getName(),
                updateDocument.getDescription());
        return ResponseEntity.ok(getResponse(
                request,
                Map.of("document", document),
                "Document updated",
                HttpStatus.OK));
    }


    @GetMapping("/download/{documentName}")
    public ResponseEntity<Resource> downloadDocument(@AuthenticationPrincipal User user,
                                                     @PathVariable("documentName") String documentName,
                                                     HttpServletRequest request) throws IOException {
        var resource = documentService.getResource(documentName);
      var httpHeader = new HttpHeaders();
      httpHeader.add("File-Name", documentName);
      httpHeader.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;File-Name=%s", resource.getFilename()));
      return  ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
              .headers(httpHeader).body(resource);
    }

}
