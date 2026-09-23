package com.scootycontact.user;

import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public UserService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users; this.encoder = encoder;
    }

    @Transactional
    public AppUser register(String name, String email, String password) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalized)) throw new IllegalArgumentException("An account already exists for this email.");
        try {
            return users.save(new AppUser(name.trim(), normalized, encoder.encode(password)));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("An account already exists for this email.");
        }
    }

    public AppUser findByEmail(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }
}
