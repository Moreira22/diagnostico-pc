package com.example.diagnostico_api.repository;


import com.example.diagnostico_api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmpresaId(Long empresaId);

    List<Cliente> findByEmpresaIdAndAtivoTrue(Long empresaId);
}