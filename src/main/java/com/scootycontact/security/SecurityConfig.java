package com.scootycontact.security;

import com.scootycontact.user.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AppUserRepository users,
                                            PasswordEncoder encoder) throws Exception {
        var csrf = CookieCsrfTokenRepository.withHttpOnlyFalse();
        var csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName("_csrf");
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/contact.html",
                        "/auth.css", "/auth.js", "/api/auth/register", "/oauth2/**", "/login/**",
                        "/css/**", "/js/**", "/images/**", "/style.css").permitAll()
                .requestMatchers("/dashboard.html", "/api/me", "/api/auth/logout").authenticated()
                .anyRequest().permitAll())
            .csrf(c -> c.csrfTokenRepository(csrf).csrfTokenRequestHandler(csrfHandler))
            .formLogin(form -> form
                .loginPage("/login.html").loginProcessingUrl("/api/auth/login")
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                .failureHandler((req, res, ex) -> { res.setStatus(401); res.setContentType("application/json"); res.getWriter().print("{\"message\":\"Invalid email or password.\"}"); }))
            .logout(logout -> logout.logoutUrl("/api/auth/logout").logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
            .oauth2Login(oauth -> oauth
                .loginPage("/login.html")
                .successHandler((req, res, auth) -> {
                    OAuth2User principal = (OAuth2User) auth.getPrincipal();
                    String email = principal.getAttribute("email");
                    String name = principal.getAttribute("name");
                    if (email != null && !users.existsByEmailIgnoreCase(email)) {
                        AppUser user = new AppUser(name == null ? email : name, email, encoder.encode(java.util.UUID.randomUUID().toString()));
                        user.setProvider("GOOGLE"); users.save(user);
                    }
                    res.sendRedirect("/dashboard.html");
                }))
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(1));
        return http.build();
    }
}
