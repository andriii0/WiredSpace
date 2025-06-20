package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.main.wiredspaceapi.business.impl.UserDeletionService;
import org.main.wiredspaceapi.domain.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDeletionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuthenticatedUserProvider userProvider;

    @InjectMocks
    private UserDeletionService userDeletionService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setEmail("user@example.com");

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
    }

    @Test
    void deleteUserCompletely_shouldDeleteAllUserData() {
        // Act
        userDeletionService.deleteUserCompletely(userId);

        // Assert
        verify(userProvider).validateCurrentUserAccess(userId);
        verify(commentRepository).deleteAllByUserId(userId);
        verify(postRepository).deleteAllByUserId(userId);
        verify(messageRepository).deleteAllConversationsForUser(user.getEmail());
        verify(userRepository).deleteUser(userId);
    }

}
