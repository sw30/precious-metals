package com.pm.preciousmetals.api.controller;

import com.pm.preciousmetals.api.request.PriceSignalRequest;
import com.pm.preciousmetals.domain.Price;
import com.pm.preciousmetals.domain.PriceSignal;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/price-signal")
public class PriceSignalController {

    @PostMapping("/new-price")
    public ResponseEntity<PriceSignal> newPrice(@Valid @RequestBody PriceSignalRequest request) {
        log.info("New price signal received: {}", request);
        
        PriceSignal domainSignal = new PriceSignal(
                Price.of(request.price()), 
                request.itemType()
        );
        
        return ResponseEntity.ok(domainSignal);
    }
}
