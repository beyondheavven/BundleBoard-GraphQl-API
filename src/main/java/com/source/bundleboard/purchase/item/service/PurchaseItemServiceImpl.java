package com.source.bundleboard.purchase.item.service;

import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.repository.PurchaseItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;

    @Override
    public Mono<PurchaseItem> save(PurchaseItem purchaseItem) {
        return purchaseItemRepository.save(purchaseItem);
    }

    @Override
    public Flux<PurchaseItem> saveAll(List<PurchaseItem> items) {
        return purchaseItemRepository.saveAll(items);
    }

    @Override
    public Flux<PurchaseItem> findAllByPurchaseId(Long id) {
        return purchaseItemRepository.findAllByPurchaseId(id);
    }
}
