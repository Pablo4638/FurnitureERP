package group2.projecte2.controladors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import group2.projecte2.dto.CanviContrasenyaDTO;
import group2.projecte2.model.FotoPerfil;
import group2.projecte2.model.User;
import group2.projecte2.repositori.jpa.UserRepository;
import group2.projecte2.serveis.FotoPerfilServei;
import group2.projecte2.serveis.UsuariServei;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class Configuracio {

    @Autowired
    private FotoPerfilServei fotoPerfilServei;

    @Autowired
    private UsuariServei usuariServei;

    @Autowired
    UserRepository userRepository;

    /**
     * Mostra el perfil de l'usuari autenticat.
     *
     * @param model          el model utilitzat per afegir atributs a la vista
     * @param authentication l'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra el perfil de l'usuari
     */
    @GetMapping("/perfil")
    public String mostrarPerfilUsuari(Model model, Authentication authentication) {
        String username = authentication.getName();
        User usuario = usuariServei.buscarPerUsuari(username);
        model.addAttribute("nomUsuari", username);
        model.addAttribute("username", username);
        model.addAttribute("user", usuario);
        return "configuracio/perfilUsuari";
    }

    /**
     * Canvia la contrasenya de l'usuari autenticat.
     *
     * @param model             El model utilitzat per a la vista.
     * @param passwordChangeDTO L'objecte que conté la nova contrasenya i la
     *                          confirmació de la contrasenya.
     * @param authentication    L'objecte d'autenticació que conté la informació de
     *                          l'usuari autenticat.
     * @return Una cadena que redirigeix a la pàgina del perfil amb un paràmetre
     *         d'error si la contrasenya no coincideix,
     *         o sense error si la contrasenya s'ha canviat correctament.
     */
    @PostMapping("/canviarContrasenya")
    public String cambiarContrasenya(Model model, @ModelAttribute CanviContrasenyaDTO passwordChangeDTO,
            Authentication authentication) {
        String password = passwordChangeDTO.getPassword();
        String passwordConfirm = passwordChangeDTO.getPasswordConfirm();
        String info = authentication.getPrincipal().toString();
        User usuari = userRepository.findById(usuariServei.extractUserId(info)).orElse(null);

        if (usuariServei.comprobarPassword(password, passwordConfirm)) {
            usuari.setPassword(passwordConfirm);
            usuariServei.encriptarContrasenya(usuari);
            usuariServei.guardar(usuari);
        } else {
            return "redirect:/perfil?error=true";
        }
        return "redirect:/perfil?error=false";
    }

    /**
     * Obté la foto de perfil d'un usuari.
     *
     * @param usuari El nom de l'usuari del qual es vol obtenir la foto de perfil.
     * @return ResponseEntity amb la foto de perfil en format byte[] i el tipus de
     *         contingut IMAGE_JPEG.
     *         Si l'usuari no té una foto de perfil, es retorna una imatge per
     *         defecte.
     *         Si hi ha un error en llegir la imatge per defecte, es retorna un
     *         estat 500.
     */
    @GetMapping("/fotoPerfil/{usuari}")
    public ResponseEntity<byte[]> obtenerFotoPerfil(@PathVariable String usuari) {
        FotoPerfil fotoPerfil = fotoPerfilServei.obtenirPerUsuari(usuari);

        if (fotoPerfil == null || fotoPerfil.getFoto() == null) {
            try {
                byte[] defaultImage = Files.readAllBytes(
                        Paths.get("src/main/resources/static/imatges/profile-1.jpg"));
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(defaultImage);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).build();
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(fotoPerfil.getFoto());
    }

    /**
     * Elimina la foto de perfil de l'usuari especificat.
     *
     * @param username El nom d'usuari del qual es vol eliminar la foto de perfil.
     * @return Una redirecció a la pàgina de perfil.
     */
    @PostMapping("/eliminarFotoPerfil/{username}")
    public String eliminarFotoPerfil(@PathVariable String username) {
        fotoPerfilServei.eliminarFotoPorUsuario(username);
        return "redirect:/perfil";
    }

    /**
     * Sube una foto de perfil para el usuario autenticado.
     *
     * @param fotoPerfil     El fitxer de la foto de perfil que es vol pujar.
     * @param authentication L'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat.
     * @return Una redirecció a la pàgina de perfil amb un paràmetre d'error que
     *         indica si la pujada ha estat exitosa o no.
     */
    @PostMapping("/subirFotoPerfil")
    public String subirFotoPerfil(@RequestParam("fotoPerfil") MultipartFile fotoPerfil, Authentication authentication) {
        try {
            String nomusuari = authentication.getName();
            byte[] fotoBytes = fotoPerfil.getBytes();

            FotoPerfil foto = new FotoPerfil(nomusuari, fotoBytes);

            fotoPerfilServei.guardar(foto);
            return "redirect:/perfil?error=false";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/perfil?error=true";
        }
    }

    /**
     * Configura l'idioma de l'aplicació segons el paràmetre proporcionat.
     *
     * @param lang     el codi de l'idioma a establir (per exemple, "en" per anglès,
     *                 "es" per espanyol)
     * @param request  l'objecte HttpServletRequest que conté la sol·licitud del
     *                 client
     * @param response l'objecte HttpServletResponse que conté la resposta del
     *                 servidor
     * @return ResponseEntity<Void> una resposta HTTP amb un estat OK (200)
     */
    @PostMapping("/setLanguage")
    public ResponseEntity<Void> setLanguage(@RequestParam("lang") String lang, HttpServletRequest request,
            HttpServletResponse response) {
        Locale locale = Locale.forLanguageTag(lang);
        LocaleContextHolder.setLocale(locale);
        request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);

        return ResponseEntity.ok().build();
    }
}