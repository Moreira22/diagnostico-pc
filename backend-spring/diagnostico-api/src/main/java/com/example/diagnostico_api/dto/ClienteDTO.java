package com.example.diagnostico_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nome;
    private String telefone;
    private Boolean ativo;
    private Long empresaId;
}
