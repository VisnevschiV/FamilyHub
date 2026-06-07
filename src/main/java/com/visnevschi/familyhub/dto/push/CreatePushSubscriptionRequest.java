package com.visnevschi.familyhub.dto.push;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePushSubscriptionRequest(

        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "^https://.*", message = "endpoint must use HTTPS")
        String endpoint,

        Long expirationTime,

        @NotNull @Valid
        Keys keys,

        @Size(max = 512)
        String userAgent,

        @Size(max = 64)
        String platform

) {
    public record Keys(
            @NotBlank @Size(max = 512) String p256dh,
            @NotBlank @Size(max = 256) String auth
    ) {}
}
