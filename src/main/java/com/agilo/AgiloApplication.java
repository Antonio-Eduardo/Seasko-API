package com.agilo;

import com.agilo.user.Usuario;
import com.agilo.user.UsuarioRepository;
import com.agilo.user.UsuarioRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AgiloApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgiloApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (repository.count() == 0) {
				Usuario admin = new Usuario();
				admin.setNome("Admin");
				admin.setUsuario("admin");
				admin.setSenha(passwordEncoder.encode("admin123"));
				admin.setRole(UsuarioRole.ADMIN);
				admin.setAtivo(true);
				repository.save(admin);
			}
		};
	}
}
