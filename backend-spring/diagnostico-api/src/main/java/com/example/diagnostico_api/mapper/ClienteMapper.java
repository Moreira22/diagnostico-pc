package com.example.diagnostico_api.mapper;


import com.example.diagnostico_api.dto.ClienteDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.diagnostico_api.entity.Cliente;

@Mapper(componentModel = "spring")
public interface ClienteMapper extends EntityMapper<ClienteDTO
        , Cliente> {

    @Override
    @Mapping(source = "empresa.id", target = "empresaId")
    ClienteDTO toDto(Cliente entity);

    @Override
    @InheritInverseConfiguration
    Cliente toEntity(ClienteDTO dto);

}
