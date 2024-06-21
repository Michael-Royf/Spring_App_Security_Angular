package com.michael.document.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface IDocument {
    Long getId();

    void setId(Long id);

    @JsonProperty("documentId")
    String getDocument_Id();

    void setDocument_Id(String documentId);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getUri();

    void setUri(String uri);

    String getIcon();

    void setIcon(String icon);

    long getSize();

    void setSize(long size);

    @JsonProperty("formattedSize")
    String getFormattedSize();

    void setFormatted_Size();

    String getExtension();

    void setExtension(String extension);

    @JsonProperty("referenceId")
    String getReference_Id();

    void setReference_Id(String referenceId);

    @JsonProperty("createdAt")
    LocalDateTime getCreated_At();

    void setCreated_At(LocalDateTime created_at);

    LocalDateTime getUpdated_At();

    @JsonProperty("updatedAt")
    void setUpdated_At(LocalDateTime updated_at);

    @JsonProperty("ownerName")
    String getOwnerName();

    void setOwnerName(String ownerName);

    @JsonProperty("ownerEmail")
    String getOwner_Email();

    void setOwner_Email(String ownerEmail);

    @JsonProperty("ownerPhone")
    String getOwner_Phone();

    void setOwner_Phone(String ownerPhone);

    @JsonProperty("ownerLastLogin")
    LocalDateTime getOwner_Last_Login();

    void setOwner_Last_Login(LocalDateTime ownerLastLogin);

    @JsonProperty("updaterName")
    String getUpdated_Name();

    void setUpdater_Name(String updaterName);


}