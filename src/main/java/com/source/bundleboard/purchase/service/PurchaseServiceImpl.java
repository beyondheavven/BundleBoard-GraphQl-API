package com.source.bundleboard.purchase.service;

import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.mapper.PurchaseMapper;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final CollectionService collectionService;

    private final PurchaseMapper purchaseMapper;

    private final PurchaseRepository purchaseRepository;

    @Override
    public Mono<List<PurchaseBaseResponse>> findAllByClientId(Long clientId) {
        return purchaseRepository.findAllByClientId(clientId)
                .flatMap(this::enrichPurchaseWithAsset)
                .collectList();
    }

    private Mono<PurchaseBaseResponse> enrichPurchaseWithAsset(Purchase purchase) {
        return collectionService.findShortResponseById(purchase.getCollectionId())
                .map(collectionMin -> purchaseMapper.toBaseResponse(purchase, collectionMin));
    }
}
