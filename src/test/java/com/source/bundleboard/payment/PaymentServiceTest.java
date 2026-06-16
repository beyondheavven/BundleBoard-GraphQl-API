package com.source.bundleboard.payment;

import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.payment.dto.PaymentRequest;
import com.source.bundleboard.payment.service.PaymentServiceImpl;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private StripeProperties stripeProperties;

    @Mock
    private CollectionService collectionService;

    @Mock
    private PurchaseService purchaseService;

    @Spy
    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private final Long userId = 42L;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest(userId, "usd", List.of(1L, 2L));
    }

    @Test
    void createCheckoutSession_NoCollectionsFound_ThrowsException() {
        when(collectionService.findAllByIds(paymentRequest.collectionIds())).thenReturn(Flux.empty());

        StepVerifier.create(paymentService.createCheckoutSession(paymentRequest))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("No collections found"))
                .verify();

        verifyNoInteractions(purchaseService, stripeProperties);
    }

    @Test
    void createCheckoutSession_TotalAmountZero_CreatesFreePurchase() {
        Collection freeCollection = new Collection();
        freeCollection.setId(1L);
        freeCollection.setPrice(BigDecimal.ZERO);

        when(collectionService.findAllByIds(paymentRequest.collectionIds())).thenReturn(Flux.just(freeCollection));
        when(purchaseService.createFreePurchase(eq(userId), eq(paymentRequest.collectionIds())))
                .thenReturn(Mono.just(mock(Purchase.class)));

        when(stripeProperties.getPaymentSuccessUrl()).thenReturn("https://bundleboard.com/success");

        StepVerifier.create(paymentService.createCheckoutSession(paymentRequest))
                .expectNext("https://bundleboard.com/success")
                .verifyComplete();

        verify(purchaseService).createFreePurchase(userId, paymentRequest.collectionIds());
    }

    @Test
    void createCheckoutSession_TotalAmountGreaterThanZero_CallsStripe() throws Exception {
        Collection paidCollection = new Collection();
        paidCollection.setId(1L);
        paidCollection.setName("Premium UI Kit");
        paidCollection.setDescription("Awesome description");
        paidCollection.setPrice(new BigDecimal("15.00"));

        when(collectionService.findAllByIds(paymentRequest.collectionIds())).thenReturn(Flux.just(paidCollection));
        when(stripeProperties.getPaymentSuccessUrl()).thenReturn("https://bundleboard.com/success");
        when(stripeProperties.getPaymentCancelUrl()).thenReturn("https://bundleboard.com/cancel");

        Session mockStripeSession = mock(Session.class);
        when(mockStripeSession.getUrl()).thenReturn("https://checkout.stripe.com/pay/cs_test_123");
        doReturn(mockStripeSession).when(paymentService).createStripeSession(any(SessionCreateParams.class));

        StepVerifier.create(paymentService.createCheckoutSession(paymentRequest))
                .expectNext("https://checkout.stripe.com/pay/cs_test_123")
                .verifyComplete();
        verify(paymentService, times(1)).createStripeSession(any(SessionCreateParams.class));
        verifyNoInteractions(purchaseService);
    }
}