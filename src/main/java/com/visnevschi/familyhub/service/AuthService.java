package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.UserAccount.UserDataDto;
import com.visnevschi.familyhub.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private UserAccountRepository userAccountrepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository userAccountrepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userAccountrepository = userAccountrepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
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

        Person person = new Person(name, role);

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(normalizedEmail);
        userAccount.setPassword(passwordEncoder.encode(rawPassword));
        userAccount.setPerson(person);

        person.setUserAccount(userAccount);

        userAccountrepository.save(userAccount);
    }

    public String login(String email, String rawPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        UserAccount account = userAccountrepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return tokenService.createToken(account);
    }

    public UserDataDto meByEmail(String email){
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        UserAccount user = userAccountrepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long familyId = (user.getPerson().getFamily() == null)
                ? null
                : user.getPerson().getFamily().getId();

        return new UserDataDto(
                user.getId(),
                user.getEmail(),
                user.getPerson().getName(),
                user.getPerson().getRole(),
                familyId
        );
    }
}
