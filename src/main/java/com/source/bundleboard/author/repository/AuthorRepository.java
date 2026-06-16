package com.source.bundleboard.author.repository;

import com.source.bundleboard.author.model.Author;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthorRepository extends R2dbcRepository<Author, Long> {

    @Query("SELECT * FROM authors WHERE users_id = :userId")
    Mono<Author> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE authors SET " +
            "total_sales = COALESCE(total_sales, 0) + 1, " +
            "rating = CASE WHEN COALESCE(rating, 0.0) < 5.0 THEN COALESCE(rating, 0.0) + 0.1 ELSE 5.0 END " +
            "WHERE id = :authorId")
    Mono<Integer> incrementSalesAndRating(Long authorId);
}
