package org.main.wiredspaceapi.controller.mapper;

import org.main.wiredspaceapi.persistence.UserRepository;
import org.mapstruct.*;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "authorName", ignore = true)
    public abstract CommentDTO toDto(Comment comment);

    public abstract Comment toEntity(CommentDTO dto);

    @AfterMapping
    protected void afterToDto(Comment comment, @MappingTarget CommentDTO dto) {
        if (userRepository != null) {
            userRepository.getUserById(comment.getAuthorId())
                    .ifPresent(user -> dto.setAuthorName(user.getName()));
        }
    }

    @Autowired
    protected UserRepository userRepository;
}
