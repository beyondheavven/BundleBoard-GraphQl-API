package com.source.bundleboard.purchase.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("purchase_items")
public class PurchaseItem {

    @Id
    @Column("id")
    private Long id;

    @Column("purchase_id")
    private Long purchaseId;

    @Column("collection_id")
    private Long collectionId;

    @Column("snapshot_price")
    private BigDecimal snapshotPrice;

    @Column("created_at")
    private Instant createdAt;
}