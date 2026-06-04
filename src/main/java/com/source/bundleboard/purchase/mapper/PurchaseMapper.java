package com.source.bundleboard.purchase.mapper;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "id", source = "purchase.id")
    @Mapping(target = "items", source = "items")
    PurchaseBaseResponse toBaseResponse(Purchase purchase, List<PurchaseItemBaseResponse> items);
}
