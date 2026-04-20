package com.source.bundleboard.image.model;

import com.source.bundleboard.mediaresource.model.MimeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("images")
public class PreviewImage {

    @Id
    @Column("id")
    Long id;

    @Column("file_name")
    String fileName;

    @Column("file_path")
    String filePath;

    @Column("mime_type")
    MimeType mimeType;

    @Column("width")
    Integer width;

    @Column("height")
    Integer height;

    @Column("file_size")
    Long fileSize;
}
