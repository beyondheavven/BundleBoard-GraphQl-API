package com.source.bundleboard.webhook.service;

import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.user.service.UserService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final PurchaseService purchaseService;

    private final UserService userService;

    private final CollectionService collectionService;

    @Override
    @Transactional
    public Mono<Void> processEvent(Event event) {
        StripeObject stripeObject = deserializeEvent(event);

        if (stripeObject == null) {
            log.error("Failed to deserialize Stripe event: {}", event.getId());
            return Mono.empty();
        }

        log.info("📥 Processing Stripe Event: {}", event.getType());

        return switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted((Session) stripeObject);
            case "checkout.session.expired" -> handleCheckoutExpired((Session) stripeObject);
            case "payment_intent.payment_failed", "payment_intent.canceled" -> handlePaymentFailed((PaymentIntent) stripeObject);
            case "charge.refunded" -> handleRefundEvent((Charge) stripeObject);
            default -> {
                log.info("⏩ Event ignored: {}", event.getType());
                yield Mono.empty();
            }
        };
    }

    private Mono<Void> handleCheckoutCompleted(Session session) {
        if (!"paid".equals(session.getPaymentStatus())) {
            log.warn("Checkout completed but not paid (status: {})", session.getPaymentStatus());
            return Mono.empty();
        }

        return purchaseService.findByStripeSessionId(session.getId())
                .flatMap(existingPurchase -> {
                    log.info("Purchase for session {} already exists. Skipping.", session.getId());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> createNewPurchase(session)))
                .then();
    }

    private Mono<Void> createNewPurchase(Session session) {
        Map<String, String> metadata = session.getMetadata();

        if (metadata == null || !metadata.containsKey("userId") || !metadata.containsKey("collectionIds")) {
            log.error("Missing metadata in Stripe Session: {}", session.getId());
            return Mono.empty();
        }

        Long userId = Long.parseLong(metadata.get("userId"));
        List<Long> collectionIds = Arrays.stream(metadata.get("collectionIds").split(","))
                .map(Long::parseLong)
                .toList();

        return userService.getUserById(userId)
                .flatMap(user -> {
                    Purchase purchase = new Purchase();
                    purchase.setUserId(user.getId());
                    purchase.setStripeSessionId(session.getId());
                    purchase.setStripePaymentIntentId(session.getPaymentIntent());

                    purchase.setAmount(BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100)));
                    purchase.setCurrency(session.getCurrency().toUpperCase());
                    purchase.setStatus(PurchaseStatus.succeeded);

                    return Flux.fromIterable(collectionIds)
                            .flatMap(collectionId -> collectionService.findById(collectionId)
                                    .map(collection -> {
                                        PurchaseItem item = new PurchaseItem();
                                        item.setCollectionId(collectionId);
                                        item.setSnapshotPrice(collection.getPrice());
                                        return item;
                                    })
                            )
                            .collectList()
                            .flatMap(items -> purchaseService.createPurchaseWithItems(purchase, items));
                })
                .doOnSuccess(p -> log.info("Successfully saved new purchase for Session: {}", session.getId()))
                .then();
    }

    private Mono<Void> handlePaymentFailed(PaymentIntent intent) {
        String errorMsg = intent.getLastPaymentError() != null
                ? intent.getLastPaymentError().getMessage()
                : "Unknown or Canceled";

        log.warn("Payment failed or canceled. Intent: {}. Reason: {}", intent.getId(), errorMsg);

        return purchaseService.findByStripePaymentIntentId(intent.getId())
                .flatMap(purchase -> {
                    purchase.setStatus(PurchaseStatus.failed);
                    return purchaseService.save(purchase);
                })
                .then();
    }

    private Mono<Void> handleRefundEvent(Charge charge) {
        String paymentIntentId = charge.getPaymentIntent();
        if (paymentIntentId == null) return Mono.empty();

        return purchaseService.findByStripePaymentIntentId(paymentIntentId)
                .flatMap(purchase -> {
                    purchase.setStatus(PurchaseStatus.refunded);
                    return purchaseService.save(purchase);
                })
                .doOnSuccess(p -> log.info("Purchase status updated to REFUNDED"))
                .then();
    }

    private Mono<Void> handleCheckoutExpired(Session session) {
        log.info("Checkout session expired for Session: {}", session.getId());
        return Mono.empty();
    }

    private StripeObject deserializeEvent(Event event) {
        EventDataObjectDeserializer objectDeserializer = event.getDataObjectDeserializer();

        if (objectDeserializer.getObject().isPresent()) {
            return objectDeserializer.getObject().get();
        } else {
            try {
                return objectDeserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                log.error("Stripe deserialization unsafe error", e);
                return null;
            }
        }
    }
}