package com.pgdi.backend.controller;

import com.pgdi.backend.dto.LoginRequestDTO;
import com.pgdi.backend.dto.LoginResponseDTO;
import com.pgdi.backend.dto.RegisterRequestDTO;
import com.pgdi.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO data) {
        try {
            LoginResponseDTO response = authService.login(data);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO data) {
        try {
            authService.register(data);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(422).body(e.getMessage());
        }
    }
}