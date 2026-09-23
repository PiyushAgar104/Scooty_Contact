package com.scootycontact.auth;

import com.scootycontact.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService users;
    public AuthController(UserService users) { this.users = users; }

    public record RegisterRequest(@NotBlank @Size(min = 2, max = 120) String name,
                                  @NotBlank @Email @Size(max = 190) String email,
                                  @NotBlank @Size(min = 8, max = 72) String password) {}

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            users.register(request.name(), request.email(), request.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Account created. You can now sign in."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        AppUser user = users.findByEmail(authentication.getName());
        return Map.of("name", user.getName(), "email", user.getEmail(), "provider", user.getProvider());
    }
}
