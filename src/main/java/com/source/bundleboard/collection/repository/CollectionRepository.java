package com.source.bundleboard.collection.repository;

import com.source.bundleboard.collection.dto.CollectionRow;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.mediaresource.model.MimeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public interface CollectionRepository extends R2dbcRepository<Collection, Long> {

    Mono<Collection> findCollectionById(Long id);

    @Query("""
    SELECT id, name, price, description FROM collections
        WHERE authors_id = :authorId
    """)
    Flux<CollectionRow> findAllByAuthorId(Long authorId);

    Flux<Collection> findAllBy(Pageable pageable);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN collection_tags ct ON c.id = ct.collections_id
        INNER JOIN tags t ON ct.tags_id = t.id
        WHERE t.name = :tagName
        LIMIT :limit OFFSET :offset
    """)
    Flux<Collection> findCollectionsByTagNamePaged(String tagName, int limit, int offset);

    @Query("""
        SELECT COUNT(c.id) FROM collections c
        INNER JOIN collection_tags ct ON c.id = ct.collections_id
        INNER JOIN tags t ON ct.tags_id = t.id
        WHERE t.name = :tagName
    """)
    Mono<Long> countCollectionsByTagName(String tagName);

    @Query("""
    SELECT c.* FROM collections c
    INNER JOIN collection_likes cl ON c.id = cl.collection_id
    WHERE cl.author_id = :authorId
    ORDER BY cl.created_at DESC
    """)
    Flux<Collection> findLikedCollectionsByAuthorId(Long authorId);

    Flux<Collection> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
    SELECT c.*, COUNT(cl.id) as likes_count
    FROM collections c
    LEFT JOIN collection_likes cl ON c.id = cl.collection_id
    GROUP BY c.id
    ORDER BY likes_count DESC
    LIMIT :limit
    """)
    Flux<Collection> findTopLikedCollections(int limit);

    @Query("""
        SELECT c.* FROM collections c
        LEFT JOIN collection_likes cl ON c.id = cl.collection_id
        GROUP BY c.id
        ORDER BY COUNT(cl.id) DESC, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findAllSortedByLikes(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        LEFT JOIN media_resources m ON c.project_file_id = m.id
        ORDER BY m.file_size ASC NULLS LAST, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findAllSortedBySizeAsc(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        LEFT JOIN authors a ON c.authors_id = a.id
        ORDER BY a.total_sales DESC NULLS LAST, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findAllSortedByAuthorSales(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        ORDER BY c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedByLatest(List<MimeType> mimeTypes, int size, int offset);

    @Query("SELECT * FROM collections ORDER BY id DESC LIMIT :size OFFSET :offset")
    Flux<Collection> findAllSortedByLatest(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        ORDER BY c.id ASC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedByOldest(List<MimeType> mimeTypes, int size, int offset);

    @Query("SELECT * FROM collections ORDER BY id ASC LIMIT :size OFFSET :offset")
    Flux<Collection> findAllSortedByOldest(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        ORDER BY c.name ASC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedByAlphabetical(List<MimeType> mimeTypes, int size, int offset);

    @Query("SELECT * FROM collections ORDER BY name ASC LIMIT :size OFFSET :offset")
    Flux<Collection> findAllSortedByAlphabetical(int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        LEFT JOIN collection_likes cl ON c.id = cl.collection_id
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        GROUP BY c.id, m.file_size
        ORDER BY COUNT(cl.id) DESC, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedByLikes(List<MimeType> mimeTypes, int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        ORDER BY m.file_size ASC NULLS LAST, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedBySizeAsc(List<MimeType> mimeTypes, int size, int offset);

    @Query("""
        SELECT c.* FROM collections c
        LEFT JOIN authors a ON c.authors_id = a.id
        INNER JOIN media_resources m ON c.project_file_id = m.id
        WHERE m.mime_type::text IN (:mimeTypes)
        ORDER BY a.total_sales DESC NULLS LAST, c.id DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<Collection> findFilteredByMimeTypesSortedByAuthorSales(List<MimeType> mimeTypes, int size, int offset);


}
