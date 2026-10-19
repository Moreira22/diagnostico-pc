package com.example.diagnostico_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Corpo da requisição de criação de usuário: os dados do {@link UsuarioDTO} mais a senha,
 * que nunca é devolvida nas respostas.
 */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioCriarDTO extends UsuarioDTO {

    private String senha;
}
