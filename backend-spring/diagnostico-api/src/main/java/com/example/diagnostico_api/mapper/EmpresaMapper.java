package com.example.diagnostico_api.mapper;



import com.example.diagnostico_api.dto.EmpresaDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import com.example.diagnostico_api.entity.Empresa;


@Mapper(componentModel = "spring")
public interface EmpresaMapper extends EntityMapper<EmpresaDTO, Empresa> {

    @Override
    EmpresaDTO toDto(Empresa entity);

    @Override
    @InheritInverseConfiguration
    Empresa toEntity(EmpresaDTO dto);

}
