package com.visnevschi.familyhub.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.dbenitity.Gender;
import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.UserAccount.AuthTokens;
import com.visnevschi.familyhub.exception.InvalidCredentialsException;
import com.visnevschi.familyhub.repository.UserAccountRepository;

@Service
public class AuthService {

    private UserAccountRepository userAccountrepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PersonaService personaService;
    private final long refreshTtlSeconds;

    public AuthService(UserAccountRepository userAccountrepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       PersonaService personaService,
                       @Value("${app.jwt.refresh-ttl-seconds}") long refreshTtlSeconds) {
        this.userAccountrepository = userAccountrepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.personaService = personaService;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public void register(String email, String rawPassword, String name, LocalDate birthday, Gender gender) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (birthday == null) {
            throw new IllegalArgumentException("Birthday is required");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userAccountrepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(normalizedEmail);
        userAccount.setPassword(passwordEncoder.encode(rawPassword));

        UserAccount savedAccount = userAccountrepository.save(userAccount);
        personaService.createForUser(savedAccount, name.trim(), birthday, gender);
    }

    public AuthTokens login(String email, String rawPassword) {
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

        return new AuthTokens(accessToken, tokenService.getTtlSeconds(), refreshToken, refreshTtlSeconds);
    }

    public AuthTokens refresh(String refreshToken) {
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

        return new AuthTokens(accessToken, tokenService.getTtlSeconds(), newRefreshToken, refreshTtlSeconds);
    }

}
