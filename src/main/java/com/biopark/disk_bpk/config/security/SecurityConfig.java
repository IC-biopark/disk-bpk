package com.biopark.disk_bpk.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @author joao.gama
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

    /**
     * Configura as liberações das rotas da API
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/resources/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .successForwardUrl("/"))
                .logout((logout) -> logout
                        .permitAll());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Informa o método de encrypt pro spring boot utilizar
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Método que configura o cors da API.
     * Aqui declaramos configurações como a origem que vamos aceitas as requisições,
     * os verbos https que podem ser utilizados, cabeçalhos que podem ser enviados
     * etc.
     *
     * @return corsConfigurationSource Bean
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept",
                "Access-Control-Allow-Credentials", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
