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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeProperties stripeProperties;

    private final CollectionService collectionService;

    private final PurchaseService purchaseService;

    @Override
    public Mono<String> createCheckoutSession(PaymentRequest input) {
        log.info(">>> Запрос на создание сессии: userId={}, currency={}, collectionIds={}",
                input.userId(), input.currency(), input.collectionIds());

        return collectionService.findAllByIds(input.collectionIds())
                .collectList()
                .doOnNext(collections -> log.info("Найдено коллекций в БД: {}", collections.size()))
                .flatMap(collections -> {
                    if (collections.isEmpty()) {
                        log.error("<<< Ошибка: Коллекции не найдены");
                        return Mono.error(new IllegalArgumentException("No collections found"));
                    }

                    try {
                        boolean containsOwnCollections = collections.stream()
                                .anyMatch(collection -> {
                                    if (collection.getAuthorId() == null) {
                                        log.warn("У коллекции {} authorId == null!", collection.getId());
                                        return false;
                                    }
                                    return collection.getAuthorId().equals(input.userId());
                                });

                        if (containsOwnCollections) {
                            log.error("<<< Ошибка: Попытка купить собственную коллекцию");
                            return Mono.error(new IllegalArgumentException("Cannot purchase own collections"));
                        }

                        boolean hasInvalidCommercialPrice = collections.stream()
                                .anyMatch(collection -> collection.getPrice() != null
                                        && collection.getPrice().compareTo(BigDecimal.ZERO) > 0
                                        && collection.getPrice().compareTo(BigDecimal.valueOf(5.00)) < 0);

                        if (hasInvalidCommercialPrice) {
                            log.error("<<< Ошибка: Неверная коммерческая цена коллекции");
                            return Mono.error(new IllegalArgumentException("Cannot purchase collections with commercial price less than $5.00"));
                        }

                        long totalAmountInCents = calculateTotalAmountInCents(collections);
                        log.info("Общая сумма в центах: {}", totalAmountInCents);

                        if (totalAmountInCents == 0) {
                            log.info("Обработка бесплатной покупки...");
                            return processFreePurchase(input.userId(), input.collectionIds());
                        }

                        log.info("Переход к созданию Stripe сессии...");
                        return processStripePayment(collections, input);

                    } catch (Exception e) {
                        // Ловим NullPointerException и прочие синхронные ошибки внутри flatMap
                        log.error("<<< Неожиданная ошибка во время проверок бизнес-логики: ", e);
                        return Mono.error(e);
                    }
                })
                .doOnSuccess(url -> log.info("<<< Сессия успешно создана. URL: {}", url))
                .doOnError(error -> log.error("<<< ИТОГОВАЯ ОШИБКА в createCheckoutSession: {}", error.getMessage(), error));
    }

    private long calculateTotalAmountInCents(List<Collection> collections) {
        return collections.stream()
                .mapToLong(collection -> collection.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                .sum();
    }

    private Mono<String> processFreePurchase(Long userId, List<Long> collectionIds) {
        return purchaseService.createFreePurchase(userId, collectionIds)
                .map(purchase -> stripeProperties.getPaymentSuccessUrl())
                .doOnError(e -> log.error("Ошибка при создании бесплатной покупки: ", e));
    }

    private Mono<String> processStripePayment(List<Collection> collections, PaymentRequest input) {
        return Mono.fromCallable(() -> {
            try {
                log.debug("Сборка параметров для Stripe...");
                SessionCreateParams params = buildSessionParams(collections, input);

                log.debug("Вызов Stripe API Session.create()...");
                Session session = createStripeSession(params);

                if (session == null || session.getUrl() == null) {
                    throw new IllegalStateException("Stripe API вернул пустую сессию или url");
                }

                log.debug("Stripe API ответил успешно");
                return session.getUrl();
            } catch (StripeException e) {
                log.error("Ошибка самого Stripe API: ", e);
                throw e; // Пробрасываем дальше, чтобы отловил GraphQL
            } catch (Exception e) {
                log.error("Ошибка при сборке параметров Stripe: ", e);
                throw e;
            }
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
        return collections.stream()
                .filter(collection -> collection.getPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(collection -> {
                    long amountInCents = collection.getPrice().movePointRight(2).longValue();
                    String truncatedDescription = collection.getDescription();
                    if (truncatedDescription != null && truncatedDescription.length() > 450) {
                        truncatedDescription = truncatedDescription.substring(0, 447) + "...";
                    }

                    return SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency.toLowerCase())
                                    .setUnitAmount(amountInCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(collection.getName())
                                            .setDescription(truncatedDescription)
                                            .build())
                                    .build())
                            .build();
                }).toList();
    }

    public Session createStripeSession(SessionCreateParams params) throws StripeException {
        return Session.create(params);
    }
}