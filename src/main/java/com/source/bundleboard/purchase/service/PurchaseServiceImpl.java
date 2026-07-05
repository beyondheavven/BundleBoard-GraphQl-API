package com.source.bundleboard.purchase.service;

import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.mediaresource.service.MediaResourceService;
import com.source.bundleboard.purchase.dto.DownloadVerificationResponse;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.service.PurchaseItemService;
import com.source.bundleboard.purchase.mapper.PurchaseMapper;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.repository.PurchaseRepository;
import com.source.bundleboard.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final CollectionService collectionService;

    private final PurchaseMapper purchaseMapper;

    private final PurchaseRepository purchaseRepository;

    private final PurchaseItemService purchaseItemService;

    private final MediaResourceService mediaResourceService;

    private final AuthorService authorService;

    @Override
    public Mono<List<PurchaseBaseResponse>> findAllByUserId(Long userId) {
        return purchaseRepository.findAllByUserId(userId)
                .flatMap(this::enrichPurchaseWithAsset)
                .collectList();
    }

    @Override
    public Mono<Purchase> findByStripeSessionId(String stripeSessionId) {
        return purchaseRepository.findByStripeSessionId(stripeSessionId);
    }

    @Override
    public Mono<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId) {
        return purchaseRepository.findByStripePaymentIntentId(stripePaymentIntentId);
    }

    @Override
    public Mono<Purchase> save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public Mono<Purchase> createPurchaseWithItems(Purchase purchase, List<PurchaseItem> items) {
        return purchaseRepository.save(purchase)
                .flatMap(savedPurchase -> {
                    items.forEach(item -> item.setPurchaseId(savedPurchase.getId()));
                    return purchaseItemService.saveAll(items).then().thenReturn(savedPurchase);
                });
    }

    @Override
    public Mono<List<PurchaseBaseResponse>> findAllLightweightByUserId(Long userId) {
        return purchaseRepository.findAllByUserId(userId)
                .map(purchase -> {
                    return new PurchaseBaseResponse(
                            purchase.getId(),
                            purchase.getAmount(),
                            purchase.getCurrency(),
                            purchase.getStatus(),
                            purchase.getCreatedAt() != null ? purchase.getCreatedAt() : null,
                            null
                    );
                })
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Purchase> createFreePurchase(Long userId, List<Long> collectionIds) {
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setStripeSessionId("FREE_" + UUID.randomUUID());
        purchase.setStripePaymentIntentId("FREE_" + UUID.randomUUID());
        purchase.setAmount(BigDecimal.ZERO);
        purchase.setCurrency("USD");
        purchase.setStatus(PurchaseStatus.succeeded);
        purchase.setCreatedAt(Instant.now());
        purchase.setUpdatedAt(Instant.now());

        return Flux.fromIterable(collectionIds)
                .flatMap(collectionId -> collectionService.findById(collectionId)
                        .map(collection -> {
                            PurchaseItem item = new PurchaseItem();
                            item.setCollectionId(collectionId);
                            item.setSnapshotPrice(BigDecimal.ZERO);
                            return Tuples.of(item, collection.getAuthorId());
                        })
                )
                .collectList()
                .flatMap(tuples -> {
                    List<PurchaseItem> items = tuples.stream()
                            .map(Tuple2::getT1)
                            .toList();

                    List<Long> authorIds = tuples.stream()
                            .map(Tuple2::getT2)
                            .toList();

                    return createPurchaseWithItems(purchase, items)
                            .flatMap(savedPurchase -> {
                                List<Mono<Void>> authorUpdated = authorIds.stream()
                                        .map(authorService::incrementSalesAndRating)
                                        .toList();
                                return Mono.when(authorUpdated).thenReturn(savedPurchase);
                            });
                });
    }

    @Override
    public Mono<DownloadVerificationResponse> verifyPurchaseForDownload(Long collectionId) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(securityContext -> Objects.requireNonNull(securityContext.getAuthentication()).getPrincipal())
                .flatMap(principal -> {
                    if (principal instanceof User user) {
                        return purchaseRepository.findByUserIdAndCollectionId(user.getId(), collectionId)
                                .flatMap(purchase -> {

                                    if (purchase.getStatus() != PurchaseStatus.succeeded) {
                                        return Mono.just(new DownloadVerificationResponse(purchase.getStatus(), null));
                                    }

                                    return collectionService.findById(collectionId)
                                            .flatMap(collection -> {
                                                if (collection.getMediaResourceId() == null) {
                                                    return Mono.just(new DownloadVerificationResponse(purchase.getStatus(), collection.getExternalLink()));
                                                }

                                                return mediaResourceService.findById(collection.getMediaResourceId())
                                                        .map(mediaResource -> new DownloadVerificationResponse(
                                                                purchase.getStatus(),
                                                                mediaResource.getFilePath()
                                                        ))
                                                        .defaultIfEmpty(new DownloadVerificationResponse(purchase.getStatus(), collection.getExternalLink()));
                                            })
                                            .defaultIfEmpty(new DownloadVerificationResponse(purchase.getStatus(), null));
                                });

                    } else {
                        return Mono.error(new RuntimeException("Unauthorized: Invalid User Principal"));
                    }
                });
    }

    @Override
    public Mono<Long> countByCollectionIdAndStatus(Long collectionId, PurchaseStatus purchaseStatus) {
        if (collectionId == null || purchaseStatus == null) {
            return Mono.empty();
        }
        return purchaseRepository.countByCollectionIdAndStatus(collectionId, purchaseStatus);
    }

    private Mono<PurchaseBaseResponse> enrichPurchaseWithAsset(Purchase purchase) {
        return purchaseItemService.findAllByPurchaseId(purchase.getId())
                .flatMap(itemDto ->
                        collectionService.findShortResponseById(itemDto.collectionId())
                                .map(collectionMin ->
                                        new PurchaseItemBaseResponse(
                                                itemDto.id(),
                                                itemDto.snapshotPrice(),
                                                itemDto.collectionId(),
                                                collectionMin
                                        )
                                )
                )
                .collectList()
                .map(enrichedItems ->
                        purchaseMapper.toBaseResponse(purchase, enrichedItems)
                );
    }
}
