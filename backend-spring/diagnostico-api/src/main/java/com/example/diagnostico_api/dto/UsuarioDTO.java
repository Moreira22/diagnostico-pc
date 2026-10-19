package com.example.diagnostico_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String tipoTecnico;
    private Boolean ativo;
    private Boolean primeiroAcesso;
    private Long empresaId;
}