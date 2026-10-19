package com.example.diagnostico_api.mapper;

import com.example.diagnostico_api.dto.UsuarioDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.diagnostico_api.entity.Usuario;


@Mapper(componentModel = "spring")
public interface UsuarioMapper extends EntityMapper<UsuarioDTO, Usuario> {

    @Override
    @Mapping(source = "perfil.id", target = "perfilId")
    @Mapping(source = "perfil.descricao", target = "perfilDescricao")
    UsuarioDTO toDto(Usuario entity);

    @Override
    @InheritInverseConfiguration
    @Mapping(target = "senha", ignore = true)
    Usuario toEntity(UsuarioDTO dto);
}