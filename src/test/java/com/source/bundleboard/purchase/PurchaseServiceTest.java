package com.source.bundleboard.purchase;

import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.service.MediaResourceService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.service.PurchaseItemService;
import com.source.bundleboard.purchase.mapper.PurchaseMapper;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.repository.PurchaseRepository;
import com.source.bundleboard.purchase.service.PurchaseServiceImpl;
import com.source.bundleboard.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock private CollectionService collectionService;
    @Mock private PurchaseMapper purchaseMapper;
    @Mock private PurchaseRepository purchaseRepository;
    @Mock private PurchaseItemService purchaseItemService;
    @Mock private MediaResourceService mediaResourceService;
    @Mock private AuthorService authorService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Purchase samplePurchase;
    private User sampleUser;
    private final Long userId = 100L;
    private final Long collectionId = 501L;

    @BeforeEach
    void setUp() {
        samplePurchase = new Purchase();
        samplePurchase.setId(1L);
        samplePurchase.setUserId(userId);
        samplePurchase.setAmount(new BigDecimal("99.99"));
        samplePurchase.setCurrency("USD");
        samplePurchase.setStatus(PurchaseStatus.succeeded);
        samplePurchase.setCreatedAt(Instant.now());

        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setUsername("test_user");
    }

    // --- BASIC CRUD & FINDERS ---

    @Test
    void findByStripeSessionId_Success() {
        when(purchaseRepository.findByStripeSessionId("session_123")).thenReturn(Mono.just(samplePurchase));

        StepVerifier.create(purchaseService.findByStripeSessionId("session_123"))
                .expectNext(samplePurchase)
                .verifyComplete();
    }

    @Test
    void findByStripePaymentIntentId_Success() {
        when(purchaseRepository.findByStripePaymentIntentId("pi_123")).thenReturn(Mono.just(samplePurchase));

        StepVerifier.create(purchaseService.findByStripePaymentIntentId("pi_123"))
                .expectNext(samplePurchase)
                .verifyComplete();
    }

    @Test
    void save_Success() {
        when(purchaseRepository.save(samplePurchase)).thenReturn(Mono.just(samplePurchase));

        StepVerifier.create(purchaseService.save(samplePurchase))
                .expectNext(samplePurchase)
                .verifyComplete();
    }

    @Test
    void countByCollectionIdAndStatus_Success() {
        when(purchaseRepository.countByCollectionIdAndStatus(collectionId, PurchaseStatus.succeeded))
                .thenReturn(Mono.just(5L));

        StepVerifier.create(purchaseService.countByCollectionIdAndStatus(collectionId, PurchaseStatus.succeeded))
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void findAllLightweightByUserId_Success() {
        when(purchaseRepository.findAllByUserId(userId)).thenReturn(Flux.just(samplePurchase));

        StepVerifier.create(purchaseService.findAllLightweightByUserId(userId))
                .assertNext(list -> {
                    assertEquals(1, list.size());
                    assertEquals(samplePurchase.getId(), list.get(0).id());
                    assertNull(list.get(0).items());
                })
                .verifyComplete();
    }

    @Test
    void findAllByUserId_Success_EnrichesWithAssets() {
        PurchaseItemBaseResponse itemResponse = new PurchaseItemBaseResponse(
                11L,
                new BigDecimal("99.99"),
                collectionId,
                null
        );

        CollectionShortResponse mockAsset = new CollectionShortResponse(collectionId, "Test Collection", null);
        PurchaseBaseResponse mockResponse = new PurchaseBaseResponse(
                1L, new BigDecimal("99.99"), "USD", PurchaseStatus.succeeded, Instant.now(), List.of()
        );

        when(purchaseRepository.findAllByUserId(userId)).thenReturn(Flux.just(samplePurchase));
        when(purchaseItemService.findAllByPurchaseId(1L)).thenReturn(Flux.just(itemResponse));
        when(collectionService.findShortResponseById(collectionId)).thenReturn(Mono.just(mockAsset));
        when(purchaseMapper.toBaseResponse(eq(samplePurchase), anyList())).thenReturn(mockResponse);

        StepVerifier.create(purchaseService.findAllByUserId(userId))
                .assertNext(list -> {
                    assertEquals(1, list.size());
                    assertEquals(mockResponse, list.get(0));
                })
                .verifyComplete();
    }

    // --- PURCHASE CREATION LOGIC ---

    @Test
    void createPurchaseWithItems_Success() {
        PurchaseItem item = new PurchaseItem();
        item.setCollectionId(collectionId);

        when(purchaseRepository.save(samplePurchase)).thenReturn(Mono.just(samplePurchase));
        when(purchaseItemService.saveAll(anyList())).thenReturn(Flux.just(item));

        StepVerifier.create(purchaseService.createPurchaseWithItems(samplePurchase, List.of(item)))
                .expectNext(samplePurchase)
                .verifyComplete();

        assertEquals(1L, item.getPurchaseId());
        verify(purchaseItemService).saveAll(anyList());
    }

    @Test
    void createFreePurchase_Success() {
        Author currentAuthor = new Author();
        currentAuthor.setId(42L);

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setAuthorId(99L);

        when(authorService.findByUserId(userId)).thenReturn(Mono.just(currentAuthor));
        when(collectionService.findById(collectionId)).thenReturn(Mono.just(collection));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(Mono.just(samplePurchase));
        when(purchaseItemService.saveAll(anyList())).thenReturn(Flux.empty());
        when(authorService.incrementSalesAndRating(99L)).thenReturn(Mono.empty());

        StepVerifier.create(purchaseService.createFreePurchase(userId, List.of(collectionId)))
                .expectNext(samplePurchase)
                .verifyComplete();

        verify(authorService).incrementSalesAndRating(99L);
        verify(purchaseRepository).save(argThat(purchase -> purchase.getAmount().equals(BigDecimal.ZERO)));
    }

    @Test
    void createFreePurchase_SelfPurchase_ThrowsException() {
        Author currentAuthor = new Author();
        currentAuthor.setId(42L);

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setAuthorId(42L);

        when(authorService.findByUserId(userId)).thenReturn(Mono.just(currentAuthor));
        when(collectionService.findById(collectionId)).thenReturn(Mono.just(collection));

        StepVerifier.create(purchaseService.createFreePurchase(userId, List.of(collectionId)))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("Authors cannot purchase their own collections"))
                .verify();

        verifyNoInteractions(purchaseRepository, purchaseItemService);
    }

    // --- DOWNLOAD VERIFICATION ---

    @Test
    void verifyPurchaseForDownload_Success_ReturnsLink() {
        Authentication auth = new UsernamePasswordAuthenticationToken(sampleUser, null, List.of());

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setMediaResourceId(10L);

        MediaResource media = new MediaResource();
        media.setFilePath("s3://bucket/file.zip");

        when(purchaseRepository.findByUserIdAndCollectionId(userId, collectionId)).thenReturn(Mono.just(samplePurchase));
        when(collectionService.findById(collectionId)).thenReturn(Mono.just(collection));
        when(mediaResourceService.findById(10L)).thenReturn(Mono.just(media));

        StepVerifier.create(purchaseService.verifyPurchaseForDownload(collectionId)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .assertNext(response -> {
                    assertEquals(PurchaseStatus.succeeded, response.status());
                    assertEquals("s3://bucket/file.zip", response.assetPath());
                })
                .verifyComplete();
    }

    @Test
    void verifyPurchaseForDownload_Success_ReturnsExternalLink() {
        Authentication auth = new UsernamePasswordAuthenticationToken(sampleUser, null, List.of());

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setMediaResourceId(null);
        collection.setExternalLink("https://external.link/download");

        when(purchaseRepository.findByUserIdAndCollectionId(userId, collectionId)).thenReturn(Mono.just(samplePurchase));
        when(collectionService.findById(collectionId)).thenReturn(Mono.just(collection));

        StepVerifier.create(purchaseService.verifyPurchaseForDownload(collectionId)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .assertNext(response -> {
                    assertEquals(PurchaseStatus.succeeded, response.status());
                    assertEquals("https://external.link/download", response.assetPath());
                })
                .verifyComplete();

        verifyNoInteractions(mediaResourceService);
    }

    @Test
    void verifyPurchaseForDownload_NotSucceeded_ReturnsStatusOnly() {
        Authentication auth = new UsernamePasswordAuthenticationToken(sampleUser, null, List.of());
        samplePurchase.setStatus(PurchaseStatus.pending);

        when(purchaseRepository.findByUserIdAndCollectionId(userId, collectionId)).thenReturn(Mono.just(samplePurchase));

        StepVerifier.create(purchaseService.verifyPurchaseForDownload(collectionId)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .assertNext(response -> {
                    assertEquals(PurchaseStatus.pending, response.status());
                    assertNull(response.assetPath());
                })
                .verifyComplete();

        verifyNoInteractions(collectionService, mediaResourceService);
    }
}