package com.example.diagnostico_api.mapper;

import com.example.diagnostico_api.dto.ComputadorDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.diagnostico_api.entity.Computador;

@Mapper(componentModel = "spring")
public interface ComputadorMapper extends EntityMapper<ComputadorDTO, Computador> {

    @Override
    @Mapping(source = "setor.id", target = "setorId")
    ComputadorDTO toDto(Computador entity);

    @Override
    @InheritInverseConfiguration
    Computador toEntity(ComputadorDTO dto);

}