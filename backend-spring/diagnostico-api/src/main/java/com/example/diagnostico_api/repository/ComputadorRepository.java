package com.example.diagnostico_api.repository;


import com.example.diagnostico_api.entity.Computador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComputadorRepository extends JpaRepository<Computador, Long> {

    List<Computador> findBySetorId(Long setorId);

    List<Computador> findBySetorIdAndAtivoTrue(Long setorId);

    // Navega Computador -> Setor -> Cliente
    List<Computador> findBySetorClienteId(Long clienteId);

    // Navega Computador -> Setor -> Cliente -> Empresa
    List<Computador> findBySetorClienteEmpresaId(Long empresaId);
}