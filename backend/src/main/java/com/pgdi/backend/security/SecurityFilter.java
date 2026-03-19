package com.pgdi.backend.security;

import com.pgdi.backend.repository.UsuarioRepository;
import com.pgdi.backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        var token = this.recoverToken(request);
        
        if (token != null) {
            try {
                var login = tokenService.validateToken(token);
                
                if (login != null && !login.isEmpty()) {
                    var userOptional = usuarioRepository.findByUsername(login);
                    
                    if (userOptional.isPresent()) {
                        var user = userOptional.get();
                        // Importante: Passar as authorities garante que o hasAuthority no SecurityConfig funcione 
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()); 
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Se o token for inválido ou expirado, limpamos o contexto para garantir o 401 
                SecurityContextHolder.clearContext();
            }
        }
        
        // Deixa a requisição seguir. Se não houver autenticação, o entryPoint configurado no SecurityConfig lançará o 401 [cite: 18, 66]
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7).trim(); 
    }
}