package com.talantimur.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF korumasını devre dışı bırakmak için güncel API kullanımı
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/eureka/**").permitAll() // Bu yol herkese açık
                        .anyExchange().authenticated()) // Diğer tüm yollar kimlik doğrulaması gerektiriyor
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // JWT desteği için güncel API kullanımı

        return serverHttpSecurity.build(); // Güvenlik filtresi zincirini döndür
    }
}
