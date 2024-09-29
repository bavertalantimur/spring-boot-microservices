package com.talantimur.inventory_service.controller;

import com.talantimur.inventory_service.dto.InventoryResponse;
import com.talantimur.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Bu sınıfın bir REST API denetleyicisi olduğunu belirtir.
@RequestMapping("/api/inventory")  // Bu denetleyicinin temel URL'si '/api/inventory' olacak.
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;  // InventoryService sınıfının bir örneği.

    @GetMapping // HTTP GET isteği için bir endpoint tanımlanıyor. {sku-code} dinamik bir parametre.
    @ResponseStatus(HttpStatus.OK)  // Bu metod başarılı olduğunda HTTP 200 OK dönecektir.
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        // API, verilen SKU kodlarının stokta olup olmadığını kontrol eder.
        return inventoryService.isInStock(skuCode);  // Stok bilgilerini InventoryService'ten alır.
    }
}

//@RequestParam("sku-code"): İstek URL'sindeki "sku-code" parametresini bir liste olarak alır ve stok durumu kontrolü için kullanır.