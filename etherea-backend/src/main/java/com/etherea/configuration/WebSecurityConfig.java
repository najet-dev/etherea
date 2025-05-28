package com.etherea.configuration;

import com.etherea.jwt.AuthEntryPointJwt;
import com.etherea.jwt.AuthTokenFilter;
import com.etherea.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    private CorsFilter corsFilter;
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .requestMatchers("/products/add").hasRole("ADMIN")
                        .requestMatchers("/products/update").hasRole("ADMIN")
                        .requestMatchers("/products/delete/**").hasRole("ADMIN")
                        .requestMatchers("/cartItem/**").permitAll()
                        .requestMatchers("/cart/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/favorites/**").permitAll()
                        .requestMatchers("/deliveryAddresses/**").permitAll()
                        .requestMatchers("/deliveryMethods/**").permitAll()
                        .requestMatchers("/payments/**").permitAll()
                        .requestMatchers("/command/**").permitAll()
                        .requestMatchers("/cookies/**").permitAll()
                        .requestMatchers("/newsletter/send").permitAll()
                        .requestMatchers("/newsletter/**").hasRole("ADMIN")
                        .requestMatchers("/contacts/**").permitAll()
                        .requestMatchers("/resetToken/**").permitAll()
                        .requestMatchers("/volumes/**").hasRole("ADMIN")
                        .requestMatchers("/tips/**").permitAll()
                        .requestMatchers("/tips/add").hasRole("ADMIN")
                        .requestMatchers("/tips/update").hasRole("ADMIN")
                        .requestMatchers("/tips/{id}").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
