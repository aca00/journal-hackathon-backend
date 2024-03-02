package com.hack.journal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    private final LogoutHandler logoutHandler;
    @Autowired
    private final LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        customizer -> customizer
                                .requestMatchers("/images/**")
                                .permitAll()
                                .requestMatchers("/actuator/logfile")
                                .permitAll()
                                .requestMatchers("/error/**")
                                .permitAll()
                                .requestMatchers("/eod")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/upload/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/users/signup")
                                .permitAll()
                                .requestMatchers("/api/v1/users/signin")
                                .permitAll()
                                .requestMatchers("/api/v1/users/reset-password")
                                .permitAll()
                                .requestMatchers("/api/v1/users/verify")
                                .permitAll()
                                .requestMatchers("/api/v1/users/self/**")
                                .hasRole("USER")
                                .requestMatchers("/api/v1/users/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/carts/self/**")
                                .hasRole("USER")
                                .requestMatchers("/api/v1/carts/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/favourites/self/**")
                                .hasRole("USER")
                                .requestMatchers("/api/v1/orders/self/**")
                                .hasRole("USER")
                                .requestMatchers("/api/v1/orders/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/users/review/**")
                                .hasRole("USER")
                                .requestMatchers("/api/v1/demo-controller/**")
                                .permitAll()
                                .requestMatchers("/swagger-ui/**")
                                .permitAll()
                                .requestMatchers("/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/inventory/**")
                                .permitAll()
                                .requestMatchers("api/v1/diary")
                                .hasRole("USER")
                                .requestMatchers("api/v1/metrics")
                                .hasRole("USER")
                                .anyRequest()
                                .authenticated()
                ).csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(customizer -> customizer
                        .logoutUrl("/api/v1/users/signout")
                        .permitAll()
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_AGENT > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

}

