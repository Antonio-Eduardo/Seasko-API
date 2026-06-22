package com.studioweb.studio_web.config;

import com.studioweb.studio_web.user.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public Usuario getUserLogado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario;
        }

        throw new RuntimeException("Principal não é instância de Usuario");
    }

    public Long getUserId() {
        return getUserLogado().getId();
    }
}
