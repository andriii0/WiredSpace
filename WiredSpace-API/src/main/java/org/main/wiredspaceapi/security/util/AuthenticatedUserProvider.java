package org.main.wiredspaceapi.security.util;

import org.main.wiredspaceapi.security.token.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUserProvider {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getDetails() instanceof AccessToken token)) {
            throw new IllegalStateException("AccessToken not found in authentication context");
        }

        return token.getAccountId();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public void validateCurrentUserAccess(String targetEmail) {
        String currentEmail = getCurrentUserEmail();
        if (!currentEmail.equals(targetEmail)) {
            throw new SecurityException("Access denied: not your account.");
        }
    }
    public void validateCurrentUserAccess(UUID targetId) {
        UUID currentId = getCurrentUserId();
        if (!currentId.equals(targetId)) {
            throw new SecurityException("Access denied: not your account.");
        }
    }
}
