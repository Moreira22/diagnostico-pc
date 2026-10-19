package com.example.diagnostico_api.mapper;


import com.example.diagnostico_api.dto.SetorDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.diagnostico_api.entity.Setor;


@Mapper(componentModel = "spring")
public interface SetorMapper extends EntityMapper<SetorDTO, Setor> {

    @Override
    @Mapping(source = "cliente.id", target = "clienteId")
    SetorDTO toDto(Setor entity);

    @Override
    @InheritInverseConfiguration
    Setor toEntity(SetorDTO dto);

}
