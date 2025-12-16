package com.visnevschi.familyhub.controller;


import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.UserAccount.UserAccountDto;
import com.visnevschi.familyhub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserAccountDto userAccountDto){
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(userAccountDto.email());
        userAccount.setPassword(userAccountDto.password());
        authService.register(userAccount);
    }

}
