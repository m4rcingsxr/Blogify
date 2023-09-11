package com.blogify.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(
            String to,
            String username,
            TemplateName templateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom("blogify.api@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        String content = templateEngine.process(templateName.getName(), context);
        mimeMessageHelper.setText(content, true);

        mailSender.send(mimeMessage);
        log.info("Activation email sent to {}", to);
    }

    @Getter
    public enum TemplateName {
        ACTIVATE_ACCOUNT("activate_account");

        private final String name;

        TemplateName(String name) {
            this.name = name;
        }
    }
}