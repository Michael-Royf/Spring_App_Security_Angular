package com.michael.document.service;

import com.michael.document.domain.Token;
import com.michael.document.domain.TokenData;
import com.michael.document.domain.User;
import com.michael.document.enumerations.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {

    String createToken(User user, Function<Token, String> tokenFunction);

    Optional<String> extractToken(HttpServletRequest request, String cookieName);

    void addCookie(HttpServletResponse response, User user, TokenType tokenType);

    <T> T getTokenData(String token, Function<TokenData, T> tokenDataFunction);

    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
