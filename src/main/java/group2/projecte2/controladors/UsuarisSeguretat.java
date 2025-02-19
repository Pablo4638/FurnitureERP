package group2.projecte2.controladors;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group2.projecte2.model.User;
import group2.projecte2.model.Enums.Role;
import group2.projecte2.repositori.jpa.UserRepository;
import group2.projecte2.serveis.UsuariServei;

@Controller
public class UsuarisSeguretat {

    @Autowired
    private UsuariServei usuariServei;

    @Autowired
    private UserRepository usuariRepositori;

    /**
     * Mostra la pàgina d'usuaris de seguretat.
     *
     * @param model          el model utilitzat per passar dades a la vista
     * @param id             l'identificador de l'usuari (pot ser null)
     * @param authentication l'objecte d'autenticació que conté informació de
     *                       l'usuari autenticat
     * @return el nom de la vista a mostrar
     */
    @GetMapping("/usuaris")
    public String mostrarUsuaris(Model model, Long id, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        List<User> user = usuariServei.obtenirTots();
        model.addAttribute("users", user);
        model.addAttribute("tipusRol", Role.values());
        model.addAttribute("empleats", usuariRepositori.EmpleatSenseUsuari());
        return "usuarisSeguretat/usuaris";
    }

    /**
     * Crea un nou usuari i el guarda a la base de dades.
     * 
     * @param usuari             L'objecte User que representa el nou usuari a
     *                           crear.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return La redirecció a la pàgina d'usuaris.
     * @throws IllegalArgumentException Si l'empleat no està assignat a l'usuari.
     * @throws Exception                Si hi ha un error en el procés de creació de
     *                                  l'usuari.
     */
    @PostMapping("/usuaris/nou")
    public String crearUsuari(@ModelAttribute("usuari") User usuari, RedirectAttributes redirectAttributes) {
        try {
            if (usuari.getEmpleat() == null) {
                throw new IllegalArgumentException("El empleat no está asignado al usuario");
            }

            String contrasenyaTemporal = usuariServei.crearContrasenya();
            usuari.setPassword(contrasenyaTemporal);

            usuariServei.enviarMissatge(
                    usuari.getUsername(),
                    usuari.getEmpleat().getEmail(),
                    contrasenyaTemporal);

            usuariServei.encriptarContrasenya(usuari);

            usuariServei.guardarCrear(usuari);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/usuaris";
    }

    /**
     * Mostra el formulari per editar un usuari existent.
     *
     * @param id                 El identificador de l'usuari a editar.
     * @param model              L'objecte Model per passar dades a la vista.
     * @param redirectAttributes Els atributs per redirigir missatges.
     * @return El nom de la vista per editar l'usuari o redirigir a la llista
     *         d'usuaris si no es troba l'usuari.
     */
    @GetMapping("/usuaris/editar/{id}")
    public String mostrarFormulariEditarUsuari(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = usuariServei.obtenirPerId(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("rols", Role.values());
            return "usuarisSeguretat/editarUsuariFragment";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/usuaris";
        }
    }

    /**
     * Actualitza un usuari existent amb les dades proporcionades.
     *
     * @param id                 El identificador de l'usuari a actualitzar.
     * @param user               L'objecte User amb les noves dades de l'usuari.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return La redirecció a la pàgina de llistat d'usuaris.
     */
    @PostMapping("/usuaris/editar/{id}")
    public String actualitzarUsuari(@PathVariable Long id, @ModelAttribute User user,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userExistentOpt = usuariServei.obtenirPerId(id);
            if (userExistentOpt.isPresent()) {
                User usuariExistent = userExistentOpt.get();
                usuariExistent.setUsername(user.getUsername());
                usuariExistent.setRole(user.getRole());
                usuariServei.guardar(usuariExistent);
                redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al actualizar el usuario: " + e.getMessage());
        }
        return "redirect:/usuaris";
    }

    /**
     * Elimina un usuari amb l'identificador proporcionat.
     *
     * @param id                 El identificador de l'usuari a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges de feedback a la
     *                           vista.
     * @return Una cadena que redirigeix a la pàgina de llistat d'usuaris.
     */
    @GetMapping("/usuaris/eliminar/{id}")
    public String eliminarUsuari(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuariServei.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuaris";
    }

    /**
     * Endpoint per canviar la contrasenya d'un usuari.
     *
     * @param id                 El identificador de l'usuari.
     * @param redirectAttributes Atributs per redirigir missatges a la vista.
     * @return Redirecció a la pàgina d'usuaris.
     * @throws Exception Si hi ha un error durant el procés de restabliment de la
     *                   contrasenya.
     */
    @GetMapping("/usuaris/contraseña/{id}")
    public String cambiarContraseña(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = usuariServei.obtenirPerId(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String contrasenyaTemporal = usuariServei.crearContrasenya();
                user.setPassword(contrasenyaTemporal);
                user.setEsNou(false);
                usuariServei.enviarMissatge(
                        user.getUsername(),
                        user.getEmpleat().getEmail(),
                        contrasenyaTemporal);
                usuariServei.encriptarContrasenya(user);
                usuariServei.guardar(user);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Contraseña restablecida y enviada exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al restablecer la contraseña: " + e.getMessage());
        }
        return "redirect:/usuaris";
    }

}