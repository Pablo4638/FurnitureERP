package group2.projecte2.controladors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import group2.projecte2.dto.CanviContrasenyaDTO;
import group2.projecte2.model.User;
import group2.projecte2.repositori.jpa.UserRepository;
import group2.projecte2.serveis.UsuariServei;

@Controller
public class WebController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UsuariServei usuariServei;

    /**
     * Gestiona les sol·licituds GET per a les rutes "/" i "/login".
     * 
     * @param error Paràmetre opcional que indica si hi ha hagut un error en l'inici
     *              de sessió.
     * @param model Objecte Model per afegir atributs que seran accessibles a la
     *              vista.
     * @return El nom de la vista "login".
     */
    @GetMapping(value = { "/", "/login" })
    public String getLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Les credencials són incorrectes. Torna-ho a intentar.");
        }
        return "login";
    }

    /**
     * Gestiona les sol·licituds GET a l'endpoint "/user".
     *
     * @return una cadena que representa la vista "user".
     */
    @GetMapping("/user")
    public String getUser() {
        return "user";
    }

    /**
     * Mètode que gestiona les peticions GET per a la pàgina de canvi de
     * contrasenya.
     *
     * @return El nom de la vista per a la pàgina de canvi de contrasenya.
     */
    @GetMapping("/canviContrasenya")
    public String canviContrasenya() {
        return "canvi_contrasenya";
    }

    /**
     * Gestiona la sol·licitud POST per canviar la contrasenya d'un usuari.
     *
     * @param passwordChangeDTO l'objecte que conté la nova contrasenya i la
     *                          confirmació de la contrasenya.
     * @param authentication    l'objecte d'autenticació que conté la informació de
     *                          l'usuari autenticat.
     * @return una cadena que redirigeix a la pàgina del tauler de control si la
     *         contrasenya es canvia correctament,
     *         o redirigeix a la pàgina de canvi de contrasenya amb un paràmetre
     *         d'error si les contrasenyes no coincideixen.
     */
    @PostMapping("/canviContrasenya")
    public String cambiarContrasenya(@ModelAttribute CanviContrasenyaDTO passwordChangeDTO,
            Authentication authentication) {
        String password = passwordChangeDTO.getPassword();
        String passwordConfirm = passwordChangeDTO.getPasswordConfirm();
        String info = authentication.getPrincipal().toString();
        User usuari = userRepository.findById(usuariServei.extractUserId(info)).orElse(null);

        if (usuariServei.comprobarPassword(password, passwordConfirm)) {
            usuari.setPassword(passwordConfirm);
            usuariServei.encriptarContrasenya(usuari);
            usuari.setEsNou(true);
            usuariServei.guardar(usuari);
            return "redirect:/taulerDeControl";
        } else {
            return "redirect:/canviContrasenya?error=true";
        }
    }
}
