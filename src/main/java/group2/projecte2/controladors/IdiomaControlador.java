package group2.projecte2.controladors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IdiomaControlador {

    @GetMapping("/cambiarIdioma")
    public String cambiarIdioma(@RequestParam("lang") String lang) {
        return "redirect:/taulerDeControl?lang=" + lang;
    }
}
