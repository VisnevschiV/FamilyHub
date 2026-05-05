package com.visnevschi.familyhub.service;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.utils.GeneratedCode;
import com.visnevschi.familyhub.utils.GeneratedCodeRepo;

@Service
public class CodeService {
    
    private static final int CODE_LENGTH = 6;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom secureRandom = new SecureRandom();
    private final long joinCodeTtlSeconds;

    public CodeService(@Value("${app.family.join-code-ttl-seconds:900}") long joinCodeTtlSeconds) {
        this.joinCodeTtlSeconds = joinCodeTtlSeconds;
    }

    public void generateUniqueCode(GeneratedCodeRepo<?> repo, GeneratedCode codeContainer){
        String code = generateCode();
        while (repo.findByCode(code).isPresent()) {
            code = generateCode();
        }
        Instant expiresAt = Instant.now().plusSeconds(joinCodeTtlSeconds);
        codeContainer.setCode(code);
        codeContainer.setExpiresAt(expiresAt);
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(secureRandom.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }
}
