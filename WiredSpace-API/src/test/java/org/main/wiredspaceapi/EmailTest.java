package org.main.wiredspaceapi;

import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTest {

    @Autowired
    private EmailService emailService;

    private final String testEmail = "matvienkoandreus@gmail.com";

    @Test
    public void sendAccountRegisterConfirmationEmail() {
        emailService.sendAccountRegisterConfirmation(testEmail);
        System.out.println("Account registration confirmation email sent.");
    }

    @Test
    public void sendAccountDeleteConfirmationEmail() {
        emailService.sendAccountDeleteConfirmation(testEmail);
        System.out.println("Account deletion confirmation email sent.");
    }

    @Test
    public void sendNewFriendRequestConfirmationEmail() {
        emailService.sendNewFriendRequestConfirmation(testEmail);
        System.out.println("New friend request confirmation email sent.");
    }
}
