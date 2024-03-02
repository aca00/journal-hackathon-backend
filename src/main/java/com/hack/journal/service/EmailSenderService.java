package com.hack.journal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailSenderService {
    private Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    @Autowired
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendMail(String to, String subject, String body) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Email error: " + e.getMessage());
            throw new RuntimeException("Couldn't send email");
        }

    }
}
