package org.main.wiredspaceapi.controller.mapper;

import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    public abstract CommentDTO toDto(Comment comment);

    public abstract Comment toEntity(CommentDTO dto);

    public abstract List<CommentDTO> toDtoList(List<Comment> comments);

    @AfterMapping
    protected void afterToDto(Comment comment, @MappingTarget CommentDTO dto) {
        if (userRepository != null) {
            userRepository.getUserById(comment.getAuthorId())
                    .ifPresent(user -> dto.setAuthorName(user.getName()));
        }
        dto.setAuthorId(comment.getAuthorId());
        dto.setPostId(comment.getPostId());
    }

    @Autowired
    protected UserRepository userRepository;
}
