package com.source.bundleboard.mediaresource;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("media_resources")
public record MediaResource(

        @Id
        @Column("id")
        Long Id,

        @Column("file_name")
        String fileName,

        @Column("file_path")
        String filePath,

        @Column("mime_type")
        MimeType mimeType,

        @Column("media_provider")
        Provider provider,

        @Column("width")
        Integer width,

        @Column("height")
        Integer height,

        @Column("file_size")
        Long fileSize

) {
}
