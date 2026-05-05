package com.visnevschi.familyhub.service;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.dbenitity.PendingUser;
import com.visnevschi.familyhub.dto.UserAccount.RegisterRequest;
import com.visnevschi.familyhub.repository.PendingUserRepository;
import com.visnevschi.familyhub.repository.UserAccountRepository;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class PendingUserService {

    private  final PendingUserRepository pendingUserRepository;
    private final UserAccountRepository userAccountRepository;
    private  final CodeService codeService;
    private  final EmailService emailService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public PendingUserService(PendingUserRepository pendingUserRepository, UserAccountRepository userAccountRepository, CodeService codeService, EmailService emailService, AuthService authService, PasswordEncoder passwordEncoder) {
        this.pendingUserRepository = pendingUserRepository;
        this.userAccountRepository = userAccountRepository;
        this.codeService = codeService;
        this.emailService = emailService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    public void createPendingUser(RegisterRequest registerRequest) throws IllegalArgumentException {
        if (pendingUserRepository.existsByEmail(registerRequest.email()) || userAccountRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

         String normalizedEmail = registerRequest.email().trim().toLowerCase(Locale.ROOT);

        PendingUser pendingUser = new PendingUser(normalizedEmail, passwordEncoder.encode(registerRequest.password()), registerRequest.name(), registerRequest.birthday(), registerRequest.gender());
        codeService.generateUniqueCode(pendingUserRepository, pendingUser); 
        pendingUserRepository.save(pendingUser);
        emailService.sendConfirmationEmail(normalizedEmail, pendingUser.getCode());
    }

    public void confirmPendingUser(String code, String email) throws IllegalArgumentException {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        PendingUser pendingUser = pendingUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("The code has expired or the email is incorrect"));

        if (!pendingUser.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid or expired confirmation code");
        }

        authService.register(pendingUser.getEmail(), pendingUser.getPassword(), pendingUser.getName(), pendingUser.getBirthday(), pendingUser.getGender());

        pendingUserRepository.delete(pendingUser);
    }
        
}
