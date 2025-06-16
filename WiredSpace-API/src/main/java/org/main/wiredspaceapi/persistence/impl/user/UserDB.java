package org.main.wiredspaceapi.persistence.impl.user;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDB extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    void deleteUserByEmail(String email);

    @Query("""
    SELECT u FROM UserEntity u
    WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
      AND u.id <> :currentUserId
""")
    List<UserEntity> searchByNameOrEmail(@Param("query") String query,
                                         @Param("currentUserId") UUID currentUserId,
                                         Pageable pageable);

    @Query("""
    SELECT COUNT(u) FROM UserEntity u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    long countByNameOrEmail(@Param("query") String query);
}
