package com.pgdi.backend.service;

import com.pgdi.backend.dto.LoginRequestDTO;
import com.pgdi.backend.dto.LoginResponseDTO;
import com.pgdi.backend.dto.RegisterRequestDTO;
import com.pgdi.backend.model.Usuario;
import com.pgdi.backend.model.UserRole;
import com.pgdi.backend.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        var userToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        Authentication auth = this.authenticationManager.authenticate(userToken);
        var usuario = (Usuario) auth.getPrincipal();
        String token = tokenService.generateToken(usuario);
        String role = usuario.getRole().name();
        return new LoginResponseDTO(token, role);
    }

    public void register(RegisterRequestDTO data) {

        if (usuarioRepository.findByUsername(data.username()).isPresent()) {
            throw new RuntimeException("Username já está em uso");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(data.username());
        usuario.setEmail(data.email());
        usuario.setPassword(passwordEncoder.encode(data.password()));
        
        usuario.setRole(UserRole.USER);

        usuarioRepository.save(usuario);
    }
}