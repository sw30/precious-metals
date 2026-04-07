package com.pm.preciousmetals.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/price-signal")
public class PriceSignalController {

    @PostMapping("/new-price")
    public ResponseEntity<?> newPrice(@RequestBody Map<String, String> priceSignal) {
        log.info("New price signal received: {}", priceSignal);
        return ResponseEntity.ok(priceSignal);
    }
}
