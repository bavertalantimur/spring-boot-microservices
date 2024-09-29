package com.talantimur.product_service; // Paket adı, projenin hiyerarşisini belirler.

import com.fasterxml.jackson.databind.ObjectMapper; // JSON ile Java nesneleri arasında dönüşüm yapmayı sağlar.
import com.talantimur.product_service.dto.ProductRequest; // Ürün bilgilerini taşımak için kullanılan DTO sınıfı.
import com.talantimur.product_service.repository.ProductRepository; // MongoDB ile etkileşimi sağlayan repository sınıfı.
import org.junit.jupiter.api.Assertions; // Test sonuçlarını kontrol etmek için kullanılan sınıf.
import org.junit.jupiter.api.Test; // Bu sınıfın bir test metodu olduğunu belirtir.
import org.springframework.beans.factory.annotation.Autowired; // Spring'in bağımlılık enjeksiyonu yapmasını sağlar.
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // MockMvc'yi otomatik olarak konfigüre eder.
import org.springframework.boot.test.context.SpringBootTest; // Spring Boot test bağlamını başlatır.
import org.springframework.http.MediaType; // HTTP isteklerinde içerik tiplerini belirtmek için kullanılır.
import org.springframework.test.context.DynamicPropertyRegistry; // Dinamik olarak özellikler eklemek için kullanılır.
import org.springframework.test.context.DynamicPropertySource; // Dinamik özellikleri ayarlamak için kullanılan anotasyon.
import org.springframework.test.web.servlet.MockMvc; // HTTP isteklerini simüle etmek için kullanılır.
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders; // HTTP istekleri oluşturmak için kullanılan yardımcı sınıf.
import org.testcontainers.containers.MongoDBContainer; // Test sırasında MongoDB'yi Docker konteynerinde çalıştırmak için kullanılır.
import org.testcontainers.junit.jupiter.Testcontainers; // Testcontainers kütüphanesiyle test yapıldığını belirtir.

import java.math.BigDecimal; // Para birimi gibi hassas değerleri depolamak için kullanılır.

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // HTTP isteklerinin sonuç durumunu kontrol eder.

@SpringBootTest // Spring Boot bağlamında tam bir uygulama başlatır, entegrasyon testi için kullanılır.
@Testcontainers // Testcontainers kütüphanesinin testlerde kullanılacağını belirtir.
@AutoConfigureMockMvc // MockMvc'yi otomatik olarak ayarlar ve konfigüre eder.

class ProductServiceApplicationTests {

	// MongoDB'nin Docker'da çalışan bir konteynerini başlatır.
	private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.0");

	// Statik blok, MongoDB konteynerini testler başlamadan önce başlatır.
	static {
		mongoDBContainer.start();
	}

	@Autowired
	private MockMvc mockMvc; // HTTP isteklerini simüle etmek için kullanılır, Spring tarafından enjekte edilir.

	@Autowired
	private ProductRepository productRepository; // MongoDB ile etkileşimde bulunmak için kullanılan repository.

	@Autowired
	private ObjectMapper objectMapper; // Java nesneleri ile JSON formatı arasında dönüşüm yapar.

	// Dinamik olarak MongoDB URI'sini ayarlar.
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		// MongoDB konteynerinden alınan veritabanı URI'sini Spring'e tanıtır.
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
		System.out.println("hello"); // Test çalıştırıldığında bir çıktı verir.
	}

	@Test
	void shouldCreateProduct() throws Exception {
		// Ürün yaratmak için bir ProductRequest nesnesi oluşturur.
		ProductRequest productRequest = getProductRequest("iPhone 13", "Apple iPhone 13", BigDecimal.valueOf(799.99));
		// Ürün nesnesini JSON formatına çevirir.
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		// /api/product endpoint'ine POST isteği gönderir ve JSON formatında ürün verisini yollar.
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON) // İçerik tipini JSON olarak belirtir.
						.content(productRequestString)) // Ürün verisini içerik olarak yollar.
				.andExpect(status().isCreated()); // HTTP 201 durum kodunu bekler (Created).
		// Veritabanında bir ürün olup olmadığını kontrol eder, varsa testi başarılı kabul eder.
		Assertions.assertEquals(1,productRepository.findAll().size());
	}


	// Bir ürün yaratmak için kullanılan metot. Ürün adı, açıklama ve fiyat içerir.
	private ProductRequest getProductRequest(String name, String description, BigDecimal price) {
		return ProductRequest.builder()
				.name(name)
				.description(description)
				.price(price)
				.build();
	}


	/* --------*/
	@Test
	void shouldGetAllProducts() throws Exception {
		// Önce veritabanına birkaç ürün ekliyoruz.
		ProductRequest productRequest1 = getProductRequest("iPhone 13", "Apple iPhone 13", BigDecimal.valueOf(799.99));
		ProductRequest productRequest2 = getProductRequest("Samsung Galaxy S21", "Samsung Galaxy S21", BigDecimal.valueOf(699.99));

		// Ürünleri JSON formatına çeviriyoruz.
		String productRequestString1 = objectMapper.writeValueAsString(productRequest1);
		String productRequestString2 = objectMapper.writeValueAsString(productRequest2);

		// Ürünleri veritabanına eklemek için POST isteklerini simüle ediyoruz.
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString1))
				.andExpect(status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString2))
				.andExpect(status().isCreated());

		// Veritabanındaki tüm ürünleri getirmek için GET isteğini simüle ediyoruz.
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()) // HTTP 200 (OK) durumunu bekliyoruz.
				.andExpect(result -> {
					// Gelen yanıtın kaç ürün içerdiğini kontrol ediyoruz.
					String responseBody = result.getResponse().getContentAsString();
					Assertions.assertTrue(responseBody.contains("iPhone 13"));
					Assertions.assertTrue(responseBody.contains("Samsung Galaxy S21"));
				});
	}



}
