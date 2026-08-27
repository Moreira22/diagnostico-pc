package com.example.diagnostico_api.auth;


import com.example.diagnostico_api.dto.UsuarioLoginDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private UsuarioLoginDTO usuario;
    private String accessToken;
    private String refreshToken;
}
