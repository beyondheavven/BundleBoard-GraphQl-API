package com.source.bundleboard.purchase.item.mapper;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseItemMapper {

    @Mapping(target = "id", source = "purchaseItem.id")
    @Mapping(target = "asset", source = "collection")
    PurchaseItemBaseResponse toItemResponse(PurchaseItem purchaseItem, CollectionShortResponse collection);

    PurchaseItemBaseResponse toItemBaseResponse(PurchaseItem purchaseItem);
}
