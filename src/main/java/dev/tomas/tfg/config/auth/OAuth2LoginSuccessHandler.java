package dev.tomas.tfg.config.auth;

import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("given_name");
        String apellidos = oAuth2User.getAttribute("family_name");
        String fotoPerfil = oAuth2User.getAttribute("picture");

        Optional<User> existing = userRepository.findByEmail(email);

        if (existing.isEmpty()) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setEmail(email);
            user.setNombre(nombre);
            user.setApellidos(apellidos);
            user.setFotoPerfil(fotoPerfil);
            user.setFechaNacimiento(LocalDate.now());
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(email);
        response.sendRedirect("http://localhost:8080/dashboard?token=" + token);
    }
}