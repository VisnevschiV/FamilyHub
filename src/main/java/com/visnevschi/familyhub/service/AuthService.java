package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private UserAccountRepository userAccountrepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountrepository, PasswordEncoder passwordEncoder) {
        this.userAccountrepository = userAccountrepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserAccount userAccount) {
        if (userAccount == null) {
            throw new IllegalArgumentException("UserAccount is required");
        }

        String email = userAccount.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        userAccount.setEmail(normalizedEmail);

        if (userAccountrepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        String rawPassword = userAccount.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        userAccount.setPassword(passwordEncoder.encode(rawPassword));

        userAccountrepository.save(userAccount);
    }

    public void login(UserAccount userAccount) {

    }
}
