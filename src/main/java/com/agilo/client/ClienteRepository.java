package com.agilo.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Page<Cliente> findByNomeContainingIgnoreCaseOrTelefoneContaining(String nome, String telefone, Pageable pageable);
}
