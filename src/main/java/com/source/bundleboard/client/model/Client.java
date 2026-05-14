package com.source.bundleboard.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("clients")
public class Client {

    @Id
    @Column("id")
    private Long id;

    @Column("users_id")
    private Long userId;

    @Column("newsletter_subscribed")
    private Boolean newsLetterSubscription;

    @Column("preferred_language")
    private String preferredLanguage;

}
