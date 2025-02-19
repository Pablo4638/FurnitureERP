package group2.projecte2.controladors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageUrl", "/taulerDeControl");
        return "access_denied";
    }
}