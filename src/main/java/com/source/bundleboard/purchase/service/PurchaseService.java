package com.source.bundleboard.purchase.service;

import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseService {

    Mono<List<PurchaseBaseResponse>> findAllByClientId(Long clientId);

}
