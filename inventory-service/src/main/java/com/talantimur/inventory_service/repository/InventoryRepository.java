package com.talantimur.inventory_service.repository;

import com.talantimur.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    List<Inventory> findBySkuCodeIn(List<String> skuCode);

   // findBySkuCodeIn(skuCode) metodu Inventory tipinde bir liste döndürüyor ve her
    // inventory nesnesi bu List<Inventory> içindeki bir öğe oluyor. Stream API ile bu öğelere erişip, onları işliyoruz.
}
