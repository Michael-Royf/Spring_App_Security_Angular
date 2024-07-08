package com.michael.document.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Response {
    private String time;
    private int code;
    private String path;
    private HttpStatus status;
    private String message;
    private String exception;
    private Map<?, ?> data;
}
