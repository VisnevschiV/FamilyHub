package com.visnevschi.familyhub.utils;

import java.util.Optional;

public interface GeneratedCodeRepo<T> {
    public Optional<T> findByCode(String code);
}
