package com.visnevschi.familyhub.utils;

import java.time.Instant;

public interface GeneratedCode {
    public void setCode(String code);
    public String getCode();

    public void setExpiresAt(Instant expiresAt);
    public Instant getExpiresAt();
    
    default boolean isExpired() {
        return getExpiresAt().isBefore(Instant.now());
    }


}
