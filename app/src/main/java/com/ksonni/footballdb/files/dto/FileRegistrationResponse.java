package com.ksonni.footballdb.files.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class FileRegistrationResponse {

    private String id;

    private String name;

    private String mimeType;

    private Long sizeBytes;

    private ZonedDateTime created;

    private String createdBy;

}
