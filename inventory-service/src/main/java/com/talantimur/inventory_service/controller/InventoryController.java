package com.talantimur.inventory_service.controller;

import com.talantimur.inventory_service.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")

public class InventoryController {
    private InventoryService inventoryService;

    @GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
        public boolean isInStock(@PathVariable("sku-code") String skuCode) {
            return inventoryService.isInStock(skuCode);
    }
}
