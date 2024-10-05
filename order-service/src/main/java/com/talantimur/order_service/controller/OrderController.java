package com.talantimur.order_service.controller;

import com.talantimur.order_service.dto.OrderRequest;
import com.talantimur.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethod")
    //@TimeLimiter(name = "inventory")
    //@Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED));
    }


    public CompletableFuture<ResponseEntity<String>> fallbackMethod(RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>("Oops! Something went wrong, please try again later!", HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
