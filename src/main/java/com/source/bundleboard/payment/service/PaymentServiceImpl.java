package com.source.bundleboard.payment.service;

import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.payment.dto.PaymentRequest;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
                    if (collections.isEmpty()){
                        return Mono.error(new IllegalArgumentException("No collections found"));
                    }

                    long totalAmountInCents = collections.stream()
                            .mapToLong(collection -> collection.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                            .sum();

                    if (totalAmountInCents == 0) {
                        return purchaseService.createFreePurchase(input.userId(), input.collectionIds())
                                .map(purchase -> stripeProperties.getPaymentSuccessUrl());
                    }

                    return Mono.fromCallable(() -> {
                        List<SessionCreateParams.LineItem> lineItems = collections.stream().map(collection -> {
                            long amountInCents = collection.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

                            return SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(input.currency().toLowerCase())
                                            .setUnitAmount(amountInCents)
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(collection.getName())
                                                    .setDescription(collection.getDescription())
                                                    .build())
                                            .build())
                                    .build();
                        }).collect(Collectors.toList());

                        String verifiedCollectionIdsStr = collections.stream()
                                .map(collection -> collection.getId().toString())
                                .collect(Collectors.joining(","));

                        SessionCreateParams params = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(stripeProperties.getPaymentSuccessUrl())
                                .setCancelUrl(stripeProperties.getPaymentCancelUrl())
                                .addAllLineItem(lineItems)
                                .putMetadata("userId", input.userId().toString())
                                .putMetadata("collectionIds", verifiedCollectionIdsStr)
                                .build();

                        Session session = Session.create(params);
                        return session.getUrl();
                    })
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }
}
