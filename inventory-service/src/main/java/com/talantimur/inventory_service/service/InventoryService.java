package com.talantimur.inventory_service.service;

import com.talantimur.inventory_service.dto.InventoryResponse;
import com.talantimur.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service  // Bu sınıfın bir servis bileşeni olduğunu belirtir.

public class InventoryService {
    private final InventoryRepository inventoryRepository;  // InventoryRepository'den gelen veriler burada işlenir.

    // Constructor injection yöntemi ile InventoryRepository nesnesi enjekte ediliyor.
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)  // Bu metod veritabanında yalnızca okuma işlemi yapar, yazma işlemi yapmaz.
    public List<InventoryResponse> isInStock(List<String> skuCode){
        // SKU kodları listesindeki her ürünü stok durumu açısından kontrol eder.
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()  // Veritabanından SKU kodlarına göre ürünleri çeker ve Stream'e dönüştürür.
                .map(inventory ->  // Her bir Inventory nesnesini InventoryResponse'a map'ler.
                        InventoryResponse.builder().skuCode(inventory.getSkuCode())  // Her ürünün SKU kodunu ekler.
                                .isInStock(inventory.getQuantity() > 0)  // Eğer ürün miktarı sıfırdan büyükse stokta olarak işaretler.
                                .build()  // InventoryResponse nesnesini oluşturur.
                ).toList();  // Stream'i listeye çevirir ve geri döner.
    }
}
/*

findBySkuCodeIn: Bu metod veritabanından verilen SKU kodlarına göre ürün listesini çeker.
Stream API: Çekilen envanter verilerini işleyip, InventoryResponse objelerine dönüştürmek için kullanılır.
InventoryResponse.builder(): Her bir ürün için yeni bir InventoryResponse nesnesi oluşturur.
.isInStock(inventory.getQuantity() > 0): Ürünün miktarı sıfırdan büyükse, stokta olduğunu işaretler.
 */