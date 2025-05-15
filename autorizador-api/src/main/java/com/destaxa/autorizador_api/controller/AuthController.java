package com.destaxa.autorizador_api.controller;

import com.destaxa.autorizador_api.dto.AuthRequest;
import com.destaxa.autorizador_api.dto.AuthResponse;
import com.destaxa.autorizador_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorization")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping
    public AuthResponse authorize(@RequestBody AuthRequest request,
                                  @RequestHeader("x-identifier") String identifier) {
        return service.authorize(request);
    }
}
