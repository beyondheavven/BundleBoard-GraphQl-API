package com.source.bundleboard.mediaresource.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("media_resources")
public record MediaResource(

        @Id
        @Column("id")
        Long id,

        @Column("file_name")
        String fileName,

        @Column("file_path")
        String filePath,

        @Column("mime_type")
        MimeType mimeType,

        @Column("provider")
        Provider provider,

        @Column("file_size")
        Long fileSize

) {
}
