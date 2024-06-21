package com.michael.document.domain;

import lombok.*;


@Getter
@Setter
@Builder
public class Token {
    private String access;
    private String refresh;
}
