package com.talantimur.discoveryserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Uygulama yapılandırma dosyasından kullanıcı adı ve şifreyi almak için kullanılır
    @Value("${app.eureka.username}")
    private String username;

    @Value("${app.eureka.password}")
    private String password;

    // Kullanıcı bilgilerini bellekte saklamak için UserDetailsService bean'i oluşturur
    @Bean
    public UserDetailsService userDetailsService() {
        // Kullanıcı adı, şifre ve yetkileri ile birlikte UserDetails nesnesi oluşturur
        UserDetails user = User
                .withUsername(username) // Kullanıcı adı
                .password(password) // Şifre
                .authorities("USER") // Kullanıcı yetkisi
                .passwordEncoder(NoOpPasswordEncoder.getInstance()::encode) // Şifreleme yöntemi (bu durumda şifreleme yapılmaz)
                .build();

        // Bellek içi kullanıcı yöneticisini oluşturur ve kullanıcıyı ekler
        return new InMemoryUserDetailsManager(user);
    }

    // Güvenlik ayarlarını yapılandırır
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırakmak için yeni yapı
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // Tüm isteklerin kimlik doğrulaması gerektirmesini sağla
                )
                .httpBasic(withDefaults()); // Temel HTTP kimlik doğrulamasını etkinleştir

        return http.build(); // Yapılandırmayı oluştur ve döndür
    }
}
