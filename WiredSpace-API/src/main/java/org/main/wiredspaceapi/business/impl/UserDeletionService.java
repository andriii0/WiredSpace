package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDeletionService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MessageRepository messageRepository;
    private final AuthenticatedUserProvider userProvider;

    @Transactional
    public void deleteUserCompletely(UUID userId) {
        userProvider.validateCurrentUserAccess(userId);

        //POST part

        commentRepository.deleteAllByUserId(userId);
        postRepository.deleteAllByUserId(userId);
        postRepository.deleteAllByUserId(userId);

        userRepository.deleteUser(userId);
    }
}
