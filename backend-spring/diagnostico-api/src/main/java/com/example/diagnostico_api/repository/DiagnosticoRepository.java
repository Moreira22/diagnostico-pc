package com.example.diagnostico_api.repository;
import org.springframework.stereotype.Repository;

import com.example.diagnostico_api.entity.Diagnostico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long>{
    List<Diagnostico> findByMachineIdOrderByTimestampAsc(String machineId);
    
}
