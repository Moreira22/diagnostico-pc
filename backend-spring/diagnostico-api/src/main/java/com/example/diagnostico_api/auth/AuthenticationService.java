package com.example.diagnostico_api.auth;



import com.example.diagnostico_api.config.JwtService;
import com.example.diagnostico_api.dto.UsuarioLoginDTO;
import com.example.diagnostico_api.entity.Perfil;
import com.example.diagnostico_api.entity.Usuario;
import com.example.diagnostico_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Perfil perfilUser = new Perfil();
        perfilUser.setDescricao("ROLE_USER");

        var user = Usuario.builder()
                .nome(request.getName())
                .login(request.getUsername())
                .senha(passwordEncoder.encode(request.getPassword()))
                .perfil(perfilUser)
                .ativo(true)
                .build();

        usuarioRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = usuarioRepository.findByLogin(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        UsuarioLoginDTO loginData = new UsuarioLoginDTO(
                user.getId(),
                user.getLogin(),
                user.getNome(),
                user.getPerfil().getDescricao(),
                user.getPerfil().getId()
        );

        return AuthenticationResponse.builder()
                .usuario(loginData)
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
        var user = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (jwtService.isTokenValid(request.getRefreshToken(), user)) {
            var accessToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(request.getRefreshToken())
                    .build();
        }
        throw new RuntimeException("Refresh token inválido");
    }
}