package dev.tomas.tfg;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(@AuthenticationPrincipal OAuth2User principal) {
        String nombre = principal.getAttribute("name");
        return "Hola " + nombre;
    }
}