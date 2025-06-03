package dev.tomas.tfg.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    @GetMapping("/")
    public RedirectView redirectToGoogleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }
}
