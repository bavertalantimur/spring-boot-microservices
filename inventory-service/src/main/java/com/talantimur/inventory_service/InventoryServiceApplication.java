package com.talantimur.inventory_service;

import com.talantimur.inventory_service.model.Inventory; // Envanter modelini içe aktarır
import com.talantimur.inventory_service.repository.InventoryRepository; // Envanter repository'sini içe aktarır
import com.talantimur.inventory_service.service.InventoryService; // Envanter servisini içe aktarır
import org.springframework.boot.CommandLineRunner; // Uygulama başlatıldığında çalışacak komut satırı koşucusunu içe aktarır
import org.springframework.boot.SpringApplication; // Spring uygulaması başlatmak için sınıfı içe aktarır
import org.springframework.boot.autoconfigure.SpringBootApplication; // Otomatik yapılandırmayı etkinleştirmek için anotasyonu içe aktarır
import org.springframework.context.annotation.Bean; // Spring konteynerine bean tanımlamak için anotasyonu içe aktarır

@SpringBootApplication // Uygulamanın ana sınıfı, otomatik yapılandırma ve bileşen taramasını etkinleştirir
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args); // Spring uygulamasını başlatır
	}

	@Bean // Spring konteynerine bir bean tanımlamak için kullanılır
	public CommandLineRunner commandLineRunner(InventoryService inventoryService, InventoryRepository inventoryRepository) {
		return args -> { // Uygulama başlatıldığında çalışacak işlemleri tanımlar
			Inventory inventory = new Inventory(); // Yeni bir Inventory (envanter) nesnesi oluşturur
			inventory.setSkuCode("iphone_13"); // Envanter nesnesinin SKU kodunu ayarlar
			inventory.setQuantity(100); // Envanter nesnesinin miktarını 100 olarak ayarlar

			Inventory inventory1 = new Inventory(); // Yeni bir diğer Inventory nesnesi oluşturur
			inventory1.setSkuCode("iphone_13_red"); // İkinci envanter nesnesinin SKU kodunu ayarlar
			inventory1.setQuantity(0); // İkinci envanter nesnesinin miktarını 0 olarak ayarlar

			inventoryRepository.save(inventory); // İlk envanter nesnesini veritabanına kaydeder
			inventoryRepository.save(inventory1); // İkinci envanter nesnesini veritabanına kaydeder
		};
	}

}
