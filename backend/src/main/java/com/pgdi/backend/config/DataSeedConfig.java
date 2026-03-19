package com.pgdi.backend.config;

import com.pgdi.backend.model.Fila;
import com.pgdi.backend.model.TipoDocumento;
import com.pgdi.backend.model.UserRole;
import com.pgdi.backend.model.Usuario;
import com.pgdi.backend.repository.FilaRepository;
import com.pgdi.backend.repository.TipoDocumentoRepository;
import com.pgdi.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeedConfig implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final FilaRepository filaRepository; // Adicionado para cumprir o requisito de Filas
    private final PasswordEncoder passwordEncoder;

    public DataSeedConfig(UsuarioRepository usuarioRepository, 
                          TipoDocumentoRepository tipoDocumentoRepository, 
                          FilaRepository filaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.filaRepository = filaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@pgdi.com.br");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            usuarioRepository.save(admin);

            Usuario user = new Usuario();
            user.setUsername("user");
            user.setEmail("user@pgdi.com.br");
            user.setPassword(passwordEncoder.encode("User@1234"));
            user.setRole(UserRole.USER);
            usuarioRepository.save(user);
        }

        if (filaRepository.count() == 0) {
            Fila filaGeral = new Fila();
            filaGeral.setNome("Fila de Processamento Geral");
            filaGeral.setDescricao("Fila padrão para recebimento de novos documentos");
            filaGeral.setRestrita(false);
            filaRepository.save(filaGeral);
        }

        if (tipoDocumentoRepository.count() == 0) {
            criarTipo("RG", "Registro Geral - Identidade");
            criarTipo("CPF", "Cadastro de Pessoa Física");
        }

        System.out.println(">>> Seed de dados (1 ADMIN, 1 USER, Filas e Tipos) finalizado conforme o escopo!");
    }

    private void criarTipo(String nome, String descricao) {
        TipoDocumento tipo = new TipoDocumento();
        tipo.setNome(nome);
        tipo.setDescricao(descricao);
        tipoDocumentoRepository.save(tipo);
    }
}