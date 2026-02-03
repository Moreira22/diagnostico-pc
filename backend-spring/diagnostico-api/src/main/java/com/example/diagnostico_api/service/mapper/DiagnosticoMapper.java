package com.example.diagnostico_api.service.mapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import com.example.diagnostico_api.entity.Diagnostico;
import com.example.diagnostico_api.service.dto.DiagnosticoDTO;


@Mapper(componentModel = "spring")
public interface  DiagnosticoMapper extends EntityMapper<DiagnosticoDTO, Diagnostico>{
    @Override
    DiagnosticoDTO toDto(Diagnostico entity);

    @Override
    @InheritInverseConfiguration
    Diagnostico toEntity(DiagnosticoDTO dto);
    
}
