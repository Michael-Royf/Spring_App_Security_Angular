package com.michael.document.service;

public interface EmailService {
    void sendNewAccountEmail(String name, String email, String key);

    void sendPasswordResetEmail(String name, String email, String key);
}
