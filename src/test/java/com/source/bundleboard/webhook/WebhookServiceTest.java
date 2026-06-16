package com.source.bundleboard.webhook;

import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.webhook.service.WebhookServiceImpl;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WebhookServiceTest {

    @Mock private PurchaseService purchaseService;
    @Mock private UserService userService;
    @Mock private CollectionService collectionService;
    @Mock private TaskProducer taskProducer;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MailProperties mailProperties;

    @InjectMocks
    private WebhookServiceImpl webhookService;

    @BeforeEach
    void setUp() {
        lenient().when(mailProperties.getSubjects().getPurchaseReceipt()).thenReturn("Your Receipt");
        lenient().when(mailProperties.getTemplates().getPurchaseReceipt()).thenReturn("receipt-template");
    }

    // --- HELPER METHOD TO MOCK STRIPE EVENT ---
    private Event mockStripeEvent(String type, StripeObject stripeObject) {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn(type);
        when(event.getId()).thenReturn("evt_test_123");

        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.ofNullable(stripeObject));

        return event;
    }

    // --- DESERIALIZATION & ROUTING TESTS ---

    @Test
    void processEvent_NullDeserialization_ReturnsEmpty() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

        when(event.getId()).thenReturn("evt_test_123");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.empty());
        when(deserializer.deserializeUnsafe()).thenThrow(new EventDataObjectDeserializationException("err", "json"));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();
    }

    @Test
    void processEvent_UnknownEventType_ReturnsEmpty() {
        Event event = mockStripeEvent("unknown.event.type", mock(StripeObject.class));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();
    }

    @Test
    void processEvent_CheckoutCompleted_Success() {
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getPaymentIntent()).thenReturn("pi_test_123");
        when(session.getAmountTotal()).thenReturn(1500L);
        when(session.getCurrency()).thenReturn("usd");
        when(session.getMetadata()).thenReturn(Map.of(
                "userId", "42",
                "collectionIds", "101,102"
        ));

        Event event = mockStripeEvent("checkout.session.completed", session);

        User mockUser = new User();
        mockUser.setId(42L);
        mockUser.setEmail("user@example.com");
        mockUser.setUsername("testuser");

        Collection mockCollection = new Collection();
        mockCollection.setPrice(new BigDecimal("7.50"));

        Purchase savedPurchase = new Purchase();
        savedPurchase.setAmount(new BigDecimal("15.00"));
        savedPurchase.setCurrency("USD");

        when(purchaseService.findByStripeSessionId("cs_test_123")).thenReturn(Mono.empty());
        when(userService.getUserById(42L)).thenReturn(Mono.just(mockUser));
        when(collectionService.findById(anyLong())).thenReturn(Mono.just(mockCollection));
        when(purchaseService.createPurchaseWithItems(any(Purchase.class), anyList())).thenReturn(Mono.just(savedPurchase));
        when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.empty());

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verify(purchaseService).createPurchaseWithItems(any(Purchase.class), anyList());
        verify(taskProducer).sendEmailTask(any(EmailTask.class));
    }

    @Test
    void processEvent_CheckoutCompleted_EmailFails_ContinuesWithoutError() {
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getPaymentIntent()).thenReturn("pi_test_123");
        when(session.getAmountTotal()).thenReturn(1500L);
        when(session.getCurrency()).thenReturn("usd");
        when(session.getMetadata()).thenReturn(Map.of("userId", "42", "collectionIds", "101"));

        Event event = mockStripeEvent("checkout.session.completed", session);

        User mockUser = new User();
        mockUser.setId(42L);
        mockUser.setEmail("user@example.com");
        mockUser.setUsername("testuser");

        Purchase savedPurchase = new Purchase();
        savedPurchase.setAmount(BigDecimal.TEN);
        savedPurchase.setCurrency("USD");

        when(purchaseService.findByStripeSessionId("cs_test_123")).thenReturn(Mono.empty());
        when(userService.getUserById(42L)).thenReturn(Mono.just(mockUser));
        when(collectionService.findById(101L)).thenReturn(Mono.just(new Collection()));

        when(purchaseService.createPurchaseWithItems(any(), anyList())).thenReturn(Mono.just(savedPurchase));
        when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.error(new RuntimeException("RabbitMQ Down")));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();
        verify(purchaseService).createPurchaseWithItems(any(), anyList());
    }

    @Test
    void processEvent_CheckoutCompleted_NotPaid_Ignores() {
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("unpaid");

        Event event = mockStripeEvent("checkout.session.completed", session);

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verifyNoInteractions(purchaseService, userService);
    }

    @Test
    void processEvent_CheckoutCompleted_AlreadyExists_Ignores() {
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getId()).thenReturn("cs_test_123");

        Event event = mockStripeEvent("checkout.session.completed", session);

        when(purchaseService.findByStripeSessionId("cs_test_123")).thenReturn(Mono.just(new Purchase()));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verifyNoInteractions(userService, collectionService);
    }

    @Test
    void processEvent_CheckoutCompleted_MissingMetadata_Ignores() {
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("paid");
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getMetadata()).thenReturn(null);

        Event event = mockStripeEvent("checkout.session.completed", session);
        when(purchaseService.findByStripeSessionId("cs_test_123")).thenReturn(Mono.empty());

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verifyNoInteractions(userService);
    }

    // --- PAYMENT FAILED & CANCELED TESTS ---

    @Test
    void processEvent_PaymentFailed_UpdatesStatus() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_test_123");

        Event event = mockStripeEvent("payment_intent.payment_failed", intent);

        Purchase existingPurchase = new Purchase();
        existingPurchase.setStatus(PurchaseStatus.pending);

        when(purchaseService.findByStripePaymentIntentId("pi_test_123")).thenReturn(Mono.just(existingPurchase));
        when(purchaseService.save(existingPurchase)).thenReturn(Mono.just(existingPurchase));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        assertEquals(PurchaseStatus.failed, existingPurchase.getStatus());
        verify(purchaseService).save(existingPurchase);
    }

    // --- REFUNDED TESTS ---

    @Test
    void processEvent_ChargeRefunded_UpdatesStatus() {
        Charge charge = mock(Charge.class);
        when(charge.getPaymentIntent()).thenReturn("pi_test_123");

        Event event = mockStripeEvent("charge.refunded", charge);

        Purchase existingPurchase = new Purchase();
        existingPurchase.setStatus(PurchaseStatus.succeeded);

        when(purchaseService.findByStripePaymentIntentId("pi_test_123")).thenReturn(Mono.just(existingPurchase));
        when(purchaseService.save(existingPurchase)).thenReturn(Mono.just(existingPurchase));

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        assertEquals(PurchaseStatus.refunded, existingPurchase.getStatus());
        verify(purchaseService).save(existingPurchase);
    }

    @Test
    void processEvent_ChargeRefunded_NullIntent_Ignores() {
        Charge charge = mock(Charge.class);
        when(charge.getPaymentIntent()).thenReturn(null);

        Event event = mockStripeEvent("charge.refunded", charge);

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verifyNoInteractions(purchaseService);
    }

    // --- EXPIRED TESTS ---

    @Test
    void processEvent_CheckoutExpired_LogsAndReturns() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("cs_test_123");

        Event event = mockStripeEvent("checkout.session.expired", session);

        StepVerifier.create(webhookService.processEvent(event))
                .verifyComplete();

        verifyNoInteractions(purchaseService, userService);
    }
}