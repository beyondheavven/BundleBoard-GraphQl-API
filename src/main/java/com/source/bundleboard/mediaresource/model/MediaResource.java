package com.source.bundleboard.mediaresource.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("media_resources")
public class MediaResource {

    @Id
    @Column("id")
    Long id;

    @Column("file_name")
    String fileName;

    @Column("file_path")
    String filePath;

    @Column("file_type")
    MediaFileType fileType;

    @Column("mime_type")
    MimeType mimeType;

    @Column("provider")
    Provider provider;

    @Column("file_size")
    Long fileSize;

}
