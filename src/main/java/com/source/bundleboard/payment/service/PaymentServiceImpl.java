package com.source.bundleboard.payment.service;

import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.payment.dto.PaymentRequest;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeProperties stripeProperties;

    private final CollectionService collectionService;

    private final PurchaseService purchaseService;

    @Override
    public Mono<String> createCheckoutSession(PaymentRequest input) {
        return collectionService.findAllByIds(input.collectionIds())
                .collectList()
                .flatMap(collections -> {
                    if (collections.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("No collections found"));
                    }

                    long totalAmountInCents = calculateTotalAmountInCents(collections);

                    if (totalAmountInCents == 0) {
                        return processFreePurchase(input.userId(), input.collectionIds());
                    }

                    return processStripePayment(collections, input);
                });
    }

    private long calculateTotalAmountInCents(List<Collection> collections) {
        return collections.stream()
                .mapToLong(collection -> collection.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                .sum();
    }

    private Mono<String> processFreePurchase(Long userId, List<Long> collectionIds) {
        return purchaseService.createFreePurchase(userId, collectionIds)
                .map(purchase -> stripeProperties.getPaymentSuccessUrl());
    }

    private Mono<String> processStripePayment(List<Collection> collections, PaymentRequest input) {
        return Mono.fromCallable(() -> {
            SessionCreateParams params = buildSessionParams(collections, input);
            Session session = createStripeSession(params);
            return session.getUrl();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private SessionCreateParams buildSessionParams(List<Collection> collections, PaymentRequest input) {
        List<SessionCreateParams.LineItem> lineItems = buildLineItems(collections, input.currency());

        String verifiedCollectionIdsStr = collections.stream()
                .map(collection -> collection.getId().toString())
                .collect(Collectors.joining(","));

        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeProperties.getPaymentSuccessUrl())
                .setCancelUrl(stripeProperties.getPaymentCancelUrl())
                .addAllLineItem(lineItems)
                .putMetadata("userId", input.userId().toString())
                .putMetadata("collectionIds", verifiedCollectionIdsStr)
                .build();
    }

    private List<SessionCreateParams.LineItem> buildLineItems(List<Collection> collections, String currency) {
        return collections.stream().map(collection -> {
            long amountInCents = collection.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

            return SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency.toLowerCase())
                            .setUnitAmount(amountInCents)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(collection.getName())
                                    .setDescription(collection.getDescription())
                                    .build())
                            .build())
                    .build();
        }).toList();
    }

    public Session createStripeSession(SessionCreateParams params) throws StripeException {
        return Session.create(params);
    }
}