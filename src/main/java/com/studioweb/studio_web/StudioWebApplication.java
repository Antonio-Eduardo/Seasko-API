package com.studioweb.studio_web;

import com.studioweb.studio_web.user.Usuario;
import com.studioweb.studio_web.user.UsuarioRepository;
import com.studioweb.studio_web.user.UsuarioRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class StudioWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudioWebApplication.class, args);
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
	@Bean
	public CommandLineRunner init(UsuarioRepository userRepository,
								  PasswordEncoder passwordEncoder) {
		return args -> {

			String username = "Eduardo";

			userRepository.findByUsuario(username).ifPresent(user -> {

				user.setSenha(passwordEncoder.encode("admin123"));
				userRepository.save(user);

			});

		};
	}

}
