package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.mapper.CommentEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentDB postCommentDB;
    private final CommentEntityMapper mapper;

    @Override
    public Comment create(Comment comment) {
        return mapper.toDomain(postCommentDB.save(mapper.toEntity(comment)));
    }

    @Override
    public Optional<Comment> getById(Long id) {
        return postCommentDB.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Comment> getAll() {
        return mapper.toDomainList(postCommentDB.findAll());
    }

    @Override
    public List<Comment> getByPostId(Long postId) {
        return mapper.toDomainList(postCommentDB.findAllByPostId(postId));
    }

    @Override
    public Comment update(Comment comment) {
        return mapper.toDomain(postCommentDB.save(mapper.toEntity(comment)));
    }

    @Override
    public void deleteById(Long id) {
        postCommentDB.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return postCommentDB.existsById(id);
    }

    @Override
    public List<Comment> getCommentsByUserId(UUID userId) {
        return mapper.toDomainList(postCommentDB.findAllByAuthor_Id(userId));
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        postCommentDB.deleteAllByAuthor_Id(userId);
    }
}
