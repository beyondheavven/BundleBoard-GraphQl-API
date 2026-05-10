package com.source.bundleboard.author.repository;

import com.source.bundleboard.author.model.Author;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthorRepository extends R2dbcRepository<Author, Long> {

    @Query("SELECT * FROM authors WHERE users_id = :userId")
    Mono<Author> findByUserId(Long userId);

    @Query("""
        SELECT a.*, u.username 
        FROM authors a 
        JOIN users u ON a.users_id = u.id 
        WHERE a.id = :id
    """)
    Mono<Author> findAuthorWithUsernameById(Long id);

}
