package com.pm.preciousmetals.infrastructure.rest;

import com.pm.preciousmetals.api.request.PriceSignalRequest;
import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.model.Price;
import com.pm.preciousmetals.domain.model.PriceSignal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/price-signal")
@RequiredArgsConstructor
@Tag(name = "Price Signal", description = "Endpoints for managing precious metal price signals")
public class PriceSignalController {

    private final ProcessPriceSignalUseCase processPriceSignalUseCase;

    @PostMapping("/new-price")
    @Operation(summary = "Register a new price signal")
    public ResponseEntity<PriceSignal> newPrice(@Valid @RequestBody PriceSignalRequest request) {
        log.info("New price signal received: {}", request);
        
        PriceSignal domainSignal = new PriceSignal(
                Price.of(request.price()), 
                request.toMetalType()
        );
        
        PriceSignal processedSignal = processPriceSignalUseCase.processPriceSignal(domainSignal);
        
        return ResponseEntity.ok(processedSignal);
    }
}
