package com.michael.document.utils;

import com.michael.document.entity.DocumentEntity;
import com.michael.document.payload.response.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
public class DocumentUtil {

    public static DocumentResponse convertDocumentEntityToResponse(DocumentEntity documentEntity) {
        return DocumentResponse.builder()
                .id(documentEntity.getId())
                .documentId(documentEntity.getDocumentId())
                .name(documentEntity.getName())
                .description(documentEntity.getDescription())
                .uri(documentEntity.getUri())
                .size(documentEntity.getSize())
                .formattedSize(documentEntity.getFormattedSize())
                .icon(documentEntity.getIcon())
                .extension(documentEntity.getExtension())
                .referenceId(documentEntity.getReferenceId())
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .ownerName(documentEntity.getOwner().getFirstName() + " " + documentEntity.getOwner().getLastName())
                .ownerEmail(documentEntity.getOwner().getEmail())
                .ownerPhone(documentEntity.getOwner().getPhone())
                .ownerLastLogin(documentEntity.getOwner().getLastLogin())
                .build();
    }


    public static String getDocumentUri(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/document/%s", filename)).toUriString();
    }

    public static String setIcon(String fileExtension) {
        var extension = StringUtils.trimAllWhitespace(fileExtension);
        if (extension.equalsIgnoreCase("DOC") || extension.equalsIgnoreCase("DOCX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
        if (extension.equalsIgnoreCase("XLS") || extension.equalsIgnoreCase("XLSX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/excel-icon.svg";
        }
        if (extension.equalsIgnoreCase("PDF")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/pdf-icon.svg";
        } else {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
    }
}


