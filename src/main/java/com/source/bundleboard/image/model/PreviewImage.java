package com.source.bundleboard.image.model;

import com.source.bundleboard.mediaresource.MimeType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("images")
public record PreviewImage(

        @Id
        @Column("id")
        Long id,

        @Column("file_name")
        String fileName,

        @Column("file_path")
        String filePath,

        @Column("mime_type")
        MimeType mimeType,

        @Column("width")
        Integer width,

        @Column("height")
        Integer height,

        @Column("file_size")
        Long fileSize
) {
}
