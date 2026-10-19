package com.example.diagnostico_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDTO {

    private Long id;
    private String nome;
    private Boolean ativo;
}