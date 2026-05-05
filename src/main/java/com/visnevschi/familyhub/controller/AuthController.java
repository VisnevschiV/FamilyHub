package com.visnevschi.familyhub.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.UserAccount.AuthTokens;
import com.visnevschi.familyhub.dto.UserAccount.LoginResponse;
import com.visnevschi.familyhub.dto.UserAccount.RefreshRequest;
import com.visnevschi.familyhub.dto.UserAccount.RegisterRequest;
import com.visnevschi.familyhub.dto.UserAccount.RegisterResponse;
import com.visnevschi.familyhub.dto.UserAccount.UserAccountDto;
import com.visnevschi.familyhub.dto.UserAccount.ConfirmEmailRequest;
import com.visnevschi.familyhub.service.AuthService;
import com.visnevschi.familyhub.service.PendingUserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PendingUserService pendingUserService;
    private final String accessCookieName;
    private final String refreshCookieName;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(AuthService authService,
                          PendingUserService pendingUserService,
                          @Value("${app.jwt.cookie-access-name:access_token}") @NonNull String accessCookieName,
                          @Value("${app.jwt.cookie-refresh-name:refresh_token}") @NonNull String refreshCookieName,
                          @Value("${app.jwt.cookie-secure:false}") boolean cookieSecure,
                          @Value("${app.jwt.cookie-same-site:Lax}") @NonNull String cookieSameSite) {
        this.authService = authService;
        this.pendingUserService = pendingUserService;
        this.accessCookieName = Objects.requireNonNull(accessCookieName);
        this.refreshCookieName = Objects.requireNonNull(refreshCookieName);
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = Objects.requireNonNull(cookieSameSite);
    }

 //   TODO: make it throw errors
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest registerRequest){
        try {
            pendingUserService.createPendingUser(registerRequest);
            return new RegisterResponse("Registration successful");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public RegisterResponse confirmEmail(@Valid @RequestBody ConfirmEmailRequest confirmEmailRequest) {
        try {
            pendingUserService.confirmPendingUser(confirmEmailRequest.getCode(), confirmEmailRequest.getEmail());
            return new RegisterResponse("Email confirmed successfully");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or expired confirmation code");
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserAccountDto userAccountDto,
                               HttpServletResponse response){
        AuthTokens tokens = authService.login(userAccountDto.email(), userAccountDto.password());
        setAuthCookies(response, tokens);
        return new LoginResponse(tokens.ttlSeconds(), tokens.refreshTtlSeconds());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody(required = false) RefreshRequest request,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse response) {
        String refreshToken = resolveRefreshToken(httpRequest, request);
        AuthTokens tokens = authService.refresh(refreshToken);
        setAuthCookies(response, tokens);
        return new LoginResponse(tokens.ttlSeconds(), tokens.refreshTtlSeconds());
    }

    private void setAuthCookies(HttpServletResponse response, AuthTokens tokens) {
        ResponseCookie accessCookie = ResponseCookie.from(Objects.requireNonNull(accessCookieName),
                Objects.requireNonNull(tokens.accessToken()))
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(tokens.ttlSeconds())
                .sameSite(cookieSameSite)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(Objects.requireNonNull(refreshCookieName),
                Objects.requireNonNull(tokens.refreshToken()))
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/auth/refresh")
                .maxAge(tokens.refreshTtlSeconds())
                .sameSite(cookieSameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String resolveRefreshToken(HttpServletRequest request, RefreshRequest body) {
        String cookieToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshCookieName.equals(cookie.getName())) {
                    cookieToken = cookie.getValue();
                    break;
                }
            }
        }

        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }

        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }

        return null;
    }
}
