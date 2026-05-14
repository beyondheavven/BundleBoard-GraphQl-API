package com.source.bundleboard.purchase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("purchases")
public class Purchase {

    @Id
    @Column("id")
    private Long id;

    @Column("collections_id")
    private Long collectionId;

    @Column("clients_id")
    private Long clientId;

    @Column("stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column("amount")
    private Double amount;

    @Column("currency")
    private String currency;

    @Column("status")
    private PurchaseStatus status;

    @Column("snapshot_price")
    private Double snapshotPrice;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
