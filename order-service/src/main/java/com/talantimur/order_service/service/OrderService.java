package com.talantimur.order_service.service;

import com.talantimur.order_service.Mapper.OrderMapper;
import com.talantimur.order_service.dto.InventoryResponse; // Envanter durumunu almak için kullanılan DTO sınıfı
import com.talantimur.order_service.dto.OrderLineItemsDto; // Sipariş satır öğeleri için veri transfer nesnesi
import com.talantimur.order_service.dto.OrderRequest; // Sipariş isteği için veri transfer nesnesi
import com.talantimur.order_service.model.Order; // Sipariş model sınıfı
import com.talantimur.order_service.model.OrderLineItems; // Sipariş satır öğeleri model sınıfı
import com.talantimur.order_service.repository.OrderRepository; // Siparişler için JPA repository arayüzü
import lombok.RequiredArgsConstructor; // Otomatik constructor oluşturma (final alanlar için dependency injection)
import org.springframework.stereotype.Service; // Bu sınıfın bir servis katmanı olduğunu belirtir
import org.springframework.transaction.annotation.Transactional; // Metotların işlem (transaction) sınırları içinde çalışmasını sağlar
import org.springframework.web.reactive.function.client.WebClient; // Diğer mikro hizmetlere HTTP istekleri yapmak için kullanılan asenkron web istemcisi

import java.util.Arrays; // Dizilerle çalışmak için yardımcı sınıf
import java.util.List; // Liste kullanımı
import java.util.UUID; // Benzersiz tanımlayıcı oluşturmak için kullanılan sınıf

@Service // Bu sınıf bir Spring Service bileşeni olarak işaretlenir
@RequiredArgsConstructor // Gerekli bağımlılıkları otomatik olarak enjekte eder (final alanlar üzerinden)
@Transactional // Sınıf içindeki tüm metotların işlem sınırları içinde çalışmasını sağlar
public class OrderService {

    private final OrderRepository orderRepository; // Sipariş veritabanı erişimi için repository
    private final WebClient.Builder webClientBuilder; // Diğer mikro hizmetlere HTTP istekleri yapmak için kullanılan WebClient
    private final OrderMapper orderMapper;
    // Siparişi oluşturmak için ana metot
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order(); // Yeni bir Order nesnesi oluştur
        order.setOrderNumber(UUID.randomUUID().toString()); // Sipariş için benzersiz bir numara oluştur (UUID)

        // OrderLineItemsDto listesini OrderLineItems listesine dönüştür
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                // Listeyi akışa (stream) dönüştür
                .map(orderMapper::toOrderLineItems)
                //.map(this::mapToDto) // Her bir OrderLineItemsDto'yu OrderLineItems'a dönüştüren map fonksiyonunu çağır
                .toList(); // Dönüştürülmüş öğeleri listeye çevir

        order.setOrderLineItems(orderLineItems); // Dönüştürülen sipariş satır öğelerini sipariş nesnesine ekle

        // Her bir sipariş satır öğesinin SKU kodunu toplayarak bir listeye çevir
        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode) // Her bir öğeden SKU kodunu al
                .toList(); // Sonuçları liste olarak topla

        // Envanter servisine mevcut ürünlerin stok durumunu kontrol etmek için HTTP isteği gönder
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()) // Envanter servisine SKU kodlarını göndererek sorgu yap
                .retrieve() // İsteği gönder ve yanıtı al
                .bodyToMono(InventoryResponse[].class) // Yanıtın gövdesini InventoryResponse dizisine dönüştür
                .block(); // Yanıtı almak için isteği blokla (bekle)
        // Tüm ürünlerin stokta olup olmadığını kontrol et
        boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock); // Her ürün stokta mı diye kontrol et

        // Eğer tüm ürünler stokta mevcutsa, siparişi veritabanına kaydet
        if (allProductInStock) {
            orderRepository.save(order); // Siparişi veritabanına kaydet
        } else {
            // Eğer ürün stokta değilse bir hata fırlat
            throw new IllegalArgumentException("Product is not in stock");
        }
    }

    // OrderLineItemsDto'yu OrderLineItems nesnesine dönüştüren yardımcı metot
    /*private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems(); // Yeni bir OrderLineItems nesnesi oluştur
        orderLineItems.setPrice(orderLineItemsDto.getPrice()); // Fiyatı ayarla
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity()); // Miktarı ayarla
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode()); // SKU kodunu ayarla
        return orderLineItems; // Dönüştürülmüş OrderLineItems nesnesini geri döndür
    }*/
}
