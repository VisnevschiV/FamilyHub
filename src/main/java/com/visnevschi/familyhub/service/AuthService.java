package com.visnevschi.familyhub.service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.UserAccount.LoginResponse;
import com.visnevschi.familyhub.exception.InvalidCredentialsException;
import com.visnevschi.familyhub.repository.UserAccountRepository;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private UserAccountRepository userAccountrepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final long refreshTtlSeconds;

    public AuthService(UserAccountRepository userAccountrepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       @Value("${app.jwt.refresh-ttl-seconds}") long refreshTtlSeconds) {
        this.userAccountrepository = userAccountrepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public void register(String email, String rawPassword, String name, String role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userAccountrepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(normalizedEmail);
        userAccount.setPassword(passwordEncoder.encode(rawPassword));

        userAccountrepository.save(userAccount);
    }

    public LoginResponse login(String email, String rawPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        UserAccount account = userAccountrepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Wrong email"));

        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new InvalidCredentialsException("Wrong email/password");
        }

        String accessToken = tokenService.createToken(account);

        
        String refreshToken = UUID.randomUUID().toString();
        Instant refreshExpiry = Instant.now().plusSeconds(refreshTtlSeconds);

        account.setRefreshToken(refreshToken);
        account.setRefreshTokenExpiry(refreshExpiry);
        userAccountrepository.save(account);

        return new LoginResponse(accessToken, tokenService.getTtlSeconds(), refreshToken, refreshTtlSeconds);
    }

    public LoginResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        UserAccount account = userAccountrepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        Instant expiry = account.getRefreshTokenExpiry();
        if (expiry == null || Instant.now().isAfter(expiry)) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        String accessToken = tokenService.createToken(account);

        // Rotate refresh token
        String newRefreshToken = UUID.randomUUID().toString();
        Instant newExpiry = Instant.now().plusSeconds(refreshTtlSeconds);

        account.setRefreshToken(newRefreshToken);
        account.setRefreshTokenExpiry(newExpiry);
        userAccountrepository.save(account);

        return new LoginResponse(accessToken, tokenService.getTtlSeconds(), newRefreshToken, refreshTtlSeconds);
    }

}
