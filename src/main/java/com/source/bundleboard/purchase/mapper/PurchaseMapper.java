package com.source.bundleboard.purchase.mapper;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "id", source = "purchase.id")
    @Mapping(target = "asset", source = "collection")
    @Mapping(target = "snapshotPrice", source = "purchase.snapshotPrice")
    @Mapping(target = "createdAt", source = "purchase.createdAt")
    PurchaseBaseResponse toBaseResponse(Purchase purchase, CollectionShortResponse collection);
}
