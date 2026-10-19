package com.example.diagnostico_api.repository;

import com.example.diagnostico_api.entity.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetorRepository extends JpaRepository<Setor, Long> {

    List<Setor> findByClienteId(Long clienteId);

    List<Setor> findByClienteIdAndAtivoTrue(Long clienteId);
}
