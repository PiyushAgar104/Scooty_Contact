package com.scootycontact.security;

import com.scootycontact.user.AppUser;
import com.scootycontact.user.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;
    public DatabaseUserDetailsService(AppUserRepository users) { this.users = users; }
    @Override
    public UserDetails loadUserByUsername(String email) {
        AppUser user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles("USER").build();
    }
}
