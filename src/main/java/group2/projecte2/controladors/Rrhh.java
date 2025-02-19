package group2.projecte2.controladors;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group2.projecte2.model.Asistencia;
import group2.projecte2.model.Empleat;
import group2.projecte2.model.Enums.Departament;
import group2.projecte2.serveis.AsistenciaServei;
import group2.projecte2.serveis.EmpleatServei;
import group2.projecte2.serveis.NominaServei;
import group2.projecte2.serveis.implementacio.ProducteServeiImpl.ProducteNotFoundException;

@Controller
public class Rrhh {
    @Autowired
    private EmpleatServei empleatServei;

    @Autowired
    private AsistenciaServei asistenciaServei;

    @Autowired
    private NominaServei nominaServei;

    /**
     * Gestiona la petició GET per mostrar la llista d'empleats.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra la llista d'empleats
     */
    @GetMapping("/empleats")
    public String mostrarEmpleats(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Empleat> empleats;
        if (filtro != null && valor != null) {
            empleats = empleatServei.filtrarYOrdenar(filtro, valor, orden);
        } else {
            empleats = empleatServei.obtenirTots();
        }

        model.addAttribute("empleats", empleats);
        model.addAttribute("departaments", Departament.values());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);
        
        return "rrhh/empleatsLlistat";
    }

    /**
     * Crea un nou empleat i el guarda al servei d'empleats.
     * 
     * @param empleat            L'objecte Empleat que es vol crear.
     * @param redirectAttributes Les atributs de redirecció per passar missatges a
     *                           la vista.
     * @return Una redirecció a la pàgina de llistat d'empleats.
     */
    @PostMapping("/empleats/nou")
    public String crearProveidor(@ModelAttribute("empleat") Empleat empleat, RedirectAttributes redirectAttributes) {
        try {
            empleatServei.guardar(empleat);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al crear el empleado: " + e.getMessage());
        }
        return "redirect:/empleats";
    }

    /**
     * Mostra el formulari per editar un empleat.
     *
     * @param id                 El identificador de l'empleat a editar.
     * @param model              El model per passar dades a la vista.
     * @param redirectAttributes Els atributs per redirigir missatges.
     * @return El nom de la vista per editar l'empleat o redirigir a la llista
     *         d'empleats si no es troba l'empleat.
     */
    @GetMapping("empleats/editar/{id}")
    public String mostrarFormulariEditarEmpleat(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Empleat> optionalEmpleat = empleatServei.obtenirPerId(id);
        if (optionalEmpleat.isPresent()) {
            model.addAttribute("empleat", optionalEmpleat.get());
            model.addAttribute("departaments", Departament.values());
            return "rrhh/editarEmpleatFragment";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado.");
            return "redirect:/empleats";
        }
    }

    /**
     * Actualitza un empleat existent amb les dades proporcionades.
     *
     * @param id                 El identificador de l'empleat a actualitzar.
     * @param empleat            L'objecte Empleat amb les noves dades.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return La redirecció a la pàgina de llistat d'empleats.
     */
    @PostMapping("/empleats/editar/{id}")
    public String actualitzarEmpleat(@PathVariable Long id, @ModelAttribute Empleat empleat,
            RedirectAttributes redirectAttributes) {
        Empleat empleatExistent = empleatServei.obtenirPerId(id).orElse(null);
        if (empleatExistent != null) {
            empleatExistent.setNom(empleat.getNom());
            empleatExistent.setSalari(empleat.getSalari());
            empleatExistent.setData_contractacio(empleat.getData_contractacio());
            empleatExistent.setDepartament(empleat.getDepartament());
            empleatExistent.setEmail(empleat.getEmail());
            empleatExistent.setTelefon(empleat.getTelefon());
            empleatServei.guardar(empleatExistent);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado actualizado exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Hubo un error al actualizar el empleado.");
        }
        return "redirect:/empleats";
    }

    /**
     * Elimina un empleat amb l'identificador proporcionat.
     *
     * @param id                 El identificador de l'empleat a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges de feedback a la
     *                           vista.
     * @return Una cadena que redirigeix a la pàgina de llistat d'empleats.
     * @throws ProducteNotFoundException Si l'empleat no es troba.
     * @throws Exception                 Si hi ha un error durant l'eliminació de
     *                                   l'empleat.
     */
    @GetMapping("/empleats/eliminar/{id}")
    public String eliminarEmpleat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empleatServei.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "El Empleat s'ha eliminat correctament.");
        } catch (ProducteNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hi ha hagut un error al eliminar el Empleat.");
        }
        return "redirect:/empleats";
    }

    /**
     * Llista les assistències dels empleats.
     *
     * @param page           El número de pàgina a mostrar, per defecte és 0.
     * @param model          L'objecte Model per afegir atributs a la vista.
     * @param authentication L'objecte Authentication per obtenir el nom d'usuari
     *                       autenticat.
     * @return El nom de la vista per mostrar l'historial d'assistències.
     */
    @GetMapping("/empleats/asistencia")
    public String listarAsistencias(@RequestParam(defaultValue = "0") int page, Model model,
            Authentication authentication) {
        int pageSize = 16;
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        Page<Asistencia> asistencias = asistenciaServei
                .buscarTots(PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("data"))));
        model.addAttribute("asistencias", asistencias.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", asistencias.getTotalPages());
        return "rrhh/historialAsistencia";
    }

    /**
     * Filtra l'assistència dels empleats segons l'identificador proporcionat.
     *
     * @param empleatId      l'identificador de l'empleat (pot ser opcional)
     * @param model          l'objecte Model per afegir atributs a la vista
     * @param authentication l'objecte Authentication per obtenir el nom d'usuari
     *                       autenticat
     * @return el nom de la vista per seleccionar l'historial d'assistència de
     *         l'empleat
     */
    @GetMapping("/empleats/asistenciaSeleccionar")
    public String filtrarAsistencia(@RequestParam(value = "id_empleat", required = false) Long empleatId, Model model,
            Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        model.addAttribute("empleats", empleatServei.obtenirTots());
        if (empleatId != null) {
            model.addAttribute("asistencias", asistenciaServei.buscarPerEmpleat(empleatId));
        } else {
            model.addAttribute("asistencias", List.of());
        }
        model.addAttribute("id_empleat", empleatId);
        return "rrhh/historialAsistenciaSeleccionarEmpleat";
    }

}