package com.michael.document.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class JwtConfiguration {
  //  @Value("${jwt.expiration}")
    private Long expiration = 432_000_000L;

//    @Value("${jwt.secret}")
    private String secret = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
}
