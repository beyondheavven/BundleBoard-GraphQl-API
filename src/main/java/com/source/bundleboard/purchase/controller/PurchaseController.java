package com.source.bundleboard.purchase.controller;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.item.service.PurchaseItemService;
import com.source.bundleboard.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseItemService purchaseItemService;

    private final CollectionService collectionService;

    @SchemaMapping(typeName = "PurchaseBaseResponse", field = "items")
    public Flux<PurchaseItemBaseResponse> getPurchaseItems(PurchaseBaseResponse purchase) {
        return purchaseItemService.findAllByPurchaseId(purchase.id());
    }

    @SchemaMapping(typeName = "PurchaseItemBaseResponse", field = "asset")
    public Mono<CollectionShortResponse> getAssetForPurchaseItem(PurchaseItemBaseResponse item) {
        return collectionService.findShortResponseById(item.collectionId());
    }
}
