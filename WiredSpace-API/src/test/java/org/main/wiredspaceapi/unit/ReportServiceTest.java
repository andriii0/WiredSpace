package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.ReportServiceImpl;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.ReportRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UUID userId;
    private Long postId;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        postId = 1L;
        post = new Post();
        user = new User();
    }

    @Test
    void reportPost_shouldSaveReport_whenValid() {
        when(postRepository.getById(postId)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(reportRepository.alreadyReported(userId, postId)).thenReturn(false);

        reportService.reportPost(postId, userId, "spam");

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void reportPost_shouldThrow_whenPostNotFound() {
        when(postRepository.getById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class,
                () -> reportService.reportPost(postId, userId, "abuse"));
    }

    @Test
    void reportPost_shouldThrow_whenUserNotFound() {
        when(postRepository.getById(postId)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> reportService.reportPost(postId, userId, "abuse"));
    }

    @Test
    void reportPost_shouldThrow_whenAlreadyReported() {
        when(postRepository.getById(postId)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(reportRepository.alreadyReported(userId, postId)).thenReturn(true);

        assertThrows(PostAlreadyReportedExcepion.class,
                () -> reportService.reportPost(postId, userId, "duplicate"));
    }

    @Test
    void getAllReports_shouldReturnList_whenAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);
        List<Report> expected = List.of(mock(Report.class));

        when(reportRepository.findAll(0, 10)).thenReturn(expected);

        List<Report> result = reportService.getAllReports(0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getAllReports_shouldThrow_whenNotAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> reportService.getAllReports(0, 10));
    }

    @Test
    void deleteReport_shouldDelete_whenAdminAndExists() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.deleteReport(1L);

        verify(reportRepository).deleteById(1L);
    }

    @Test
    void deleteReport_shouldThrow_whenNotAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> reportService.deleteReport(1L));
    }

    @Test
    void deleteReport_shouldThrow_whenNotFound() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);
        when(reportRepository.existsById(1L)).thenReturn(false);

        assertThrows(ReportNotFoundException.class,
                () -> reportService.deleteReport(1L));
    }

    @Test
    void getAllReportsForPost_shouldReturnList_whenAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);
        List<Report> expected = List.of(mock(Report.class));
        when(reportRepository.getAllReportsForPost(postId)).thenReturn(expected);

        List<Report> result = reportService.getAllReportsForPost(postId);

        assertEquals(expected, result);
    }

    @Test
    void getAllReportsForPost_shouldThrow_whenNotAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> reportService.getAllReportsForPost(postId));
    }

    @Test
    void deleteAllByReporter_shouldDelete_whenAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);

        reportService.deleteAllByReporter(userId);

        verify(reportRepository).deleteAllByReporter(userId);
    }

    @Test
    void deleteAllByReporter_shouldThrow_whenNotAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> reportService.deleteAllByReporter(userId));
    }

    @Test
    void deleteAllByPost_shouldDelete_whenAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(true);

        reportService.deleteAllByPost(postId);

        verify(reportRepository).deleteAllByPost(postId);
    }

    @Test
    void deleteAllByPost_shouldThrow_whenNotAdmin() {
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> reportService.deleteAllByPost(postId));
    }
}
