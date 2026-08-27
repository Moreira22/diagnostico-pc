package com.example.diagnostico_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioLoginDTO {
    private Long id;
    private String login;
    private String nome;
    private String perfilDesc;
    private Long perfilId;
    private String token;

    public UsuarioLoginDTO(Long id, String login, String nome, String perfilDesc, Long perfilId) {
        this.id = id;
        this.login = login;
        this.nome = nome;
        this.perfilDesc = perfilDesc;
        this.perfilId = perfilId;
    }
}
