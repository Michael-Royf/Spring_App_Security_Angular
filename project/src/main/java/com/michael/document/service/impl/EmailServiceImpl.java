package com.michael.document.service.impl;

import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.michael.document.constant.Constants.*;
import static com.michael.document.utils.emailUtils.EmailUtils.getEmailMessage;
import static com.michael.document.utils.emailUtils.EmailUtils.getResetPasswordMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
//    @Value("${spring.mail.verify.host}")
    private String host = "http://localhost:8080";
 //   @Value("${spring.mail.username}")
    private String fromEmail = "supportRoyf@gmail.com";


    @Override
    @Async
    public void sendNewAccountEmail(String name, String email, String key) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setText(getEmailMessage(name, host, key));
            mailSender.send(message);
        } catch (Exception exception) {
            throw new ApiException(UNABLE_TO_SEND_EMAIL);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String name, String email, String key) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setText(getResetPasswordMessage(name, host, key));
            mailSender.send(message);
        } catch (Exception exception) {
            throw new ApiException(UNABLE_TO_SEND_EMAIL);
        }
    }
}
