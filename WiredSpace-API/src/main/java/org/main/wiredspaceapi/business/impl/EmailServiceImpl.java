package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.EmailService;
import org.main.wiredspaceapi.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    @Override
    public void sendAccountRegisterConfirmation(String email) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", "Your account has been successfully registered!");
        sendEmail(email, "Account Registration Confirmation", "register-confirmation", variables);
    }

    @Override
    public void sendAccountDeleteConfirmation(String email) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", "Your account has been successfully deleted. We're sorry to see you go.");
        sendEmail(email, "Account Deletion Confirmation", "delete-confirmation", variables);
    }

    @Override
    public void sendNewFriendRequestConfirmation(String email) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", "You have a new friend request on WiredSpace.");
        sendEmail(email, "New Friend Request", "friend-request", variables);
    }
}
