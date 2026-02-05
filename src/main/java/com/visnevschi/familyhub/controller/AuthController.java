package com.visnevschi.familyhub.controller;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.UserAccount.LoginResponse;
import com.visnevschi.familyhub.dto.UserAccount.RefreshRequest;
import com.visnevschi.familyhub.dto.UserAccount.RegisterRequest;
import com.visnevschi.familyhub.dto.UserAccount.UserAccountDto;
import com.visnevschi.familyhub.dto.UserAccount.UserDataDto;
import com.visnevschi.familyhub.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest registerRequest){
        authService.register(
                registerRequest.email(),
                registerRequest.password(),
                registerRequest.name(),
                registerRequest.role()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserAccountDto userAccountDto){
        return authService.login(userAccountDto.email(), userAccountDto.password());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @GetMapping("/me")
    public UserDataDto me(@AuthenticationPrincipal Jwt jwt) {
        return authService.meByEmail(jwt.getSubject());
    }
}
