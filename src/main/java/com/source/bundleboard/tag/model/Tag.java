package com.source.bundleboard.tag.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "tags")
public class Tag {

    @Id
    @Column("id")
    Long id;

    @NotNull
    @Column("name")
    String name;
}
