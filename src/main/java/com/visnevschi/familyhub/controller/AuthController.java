package com.visnevschi.familyhub.controller;


import com.visnevschi.familyhub.dto.UserAccount.LoginResponse;
import com.visnevschi.familyhub.dto.UserAccount.RegisterRequest;
import com.visnevschi.familyhub.dto.UserAccount.UserAccountDto;
import com.visnevschi.familyhub.dto.UserAccount.UserDataDto;
import com.visnevschi.familyhub.service.AuthService;
import com.visnevschi.familyhub.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
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

        String token = authService.login(userAccountDto.email(), userAccountDto.password());
        return new LoginResponse(token, tokenService.getTtlSeconds());

    }

    @GetMapping("/me")
    public UserDataDto me(@AuthenticationPrincipal Jwt jwt) {
        return authService.meByEmail(jwt.getSubject());
    }
}
