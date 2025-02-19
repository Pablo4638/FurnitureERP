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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group2.projecte2.model.CategoriaProducte;
import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Enums.UnitatMesura;
import group2.projecte2.serveis.CategoriaServei;
import group2.projecte2.serveis.MovimentInventariServei;
import group2.projecte2.serveis.ProducteServei;
import group2.projecte2.serveis.ProveidorServei;
import group2.projecte2.serveis.implementacio.ProducteServeiImpl.ProducteNotFoundException;

@Controller
public class Inventari {
    @Autowired
    private ProducteServei producteServei;

    @Autowired
    private CategoriaServei categoriaServei;

    @Autowired
    private ProveidorServei proveidorServei;

    @Autowired
    private MovimentInventariServei movimentInventariServei;

    /**
     * Gestiona la petició GET per a mostrar la llista de productes.
     * 
     * @param model          El model utilitzat per a passar dades a la vista.
     * @param authentication L'objecte d'autenticació que conté informació sobre
     *                       l'usuari autenticat.
     * @return El nom de la vista que mostra la llista de productes.
     */
    @GetMapping("/productes")
    public String mostrarFactures(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Producte> producte;
        if (filtro != null && valor != null) {
            producte = producteServei.filtrarYOrdenar(filtro, valor, orden);
        } else {
            producte = producteServei.obtenirTots();
        }

        model.addAttribute("categorias", categoriaServei.obtenirTotes());
        model.addAttribute("productes", producte);
        model.addAttribute("proveidors", proveidorServei.obtenirTots());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "inventari/productesLlistat";
    }

    /**
     * Crea un nou producte i el guarda al servei de productes.
     * 
     * @param producte           L'objecte Producte que es vol crear.
     * @param redirectAttributes Atributs per redirigir missatges flash.
     * @return Una cadena que redirigeix a la pàgina de llistat de productes.
     */
    @PostMapping("/productes/nou")
    public String crearProducte(@ModelAttribute("producte") Producte producte, RedirectAttributes redirectAttributes) {
        producte.setUnitat_mesura(UnitatMesura.Unitats);
        producteServei.guardar(producte);
        redirectAttributes.addFlashAttribute("successMessage", "El producte s'ha creat correctament.");
        return "redirect:/productes";
    }

    /**
     * Mostra el formulari per editar un producte existent.
     *
     * @param id                 El identificador del producte a editar.
     * @param model              L'objecte Model per passar dades a la vista.
     * @param redirectAttributes L'objecte RedirectAttributes per passar missatges
     *                           flash.
     * @return El nom de la vista per editar el producte si existeix, en cas
     *         contrari redirigeix a la llista de productes.
     */
    @GetMapping("/productes/editar/{id}")
    public String mostrarFormulariEditarProducte(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Producte> optionalProducte = producteServei.obtenirPerId(id);
        if (optionalProducte.isPresent()) {
            model.addAttribute("producte", optionalProducte.get());
            model.addAttribute("categorias", categoriaServei.obtenirTotes());
            return "inventari/editarProducteFragment";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "El producte no existeix.");
        return "redirect:/productes";
    }

    /**
     * Actualitza un producte existent amb les dades proporcionades.
     *
     * @param id                 El identificador del producte a actualitzar.
     * @param producte           L'objecte Producte amb les noves dades.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return La redirecció a la pàgina de llistat de productes.
     */
    @PostMapping("/productes/editar/{id}")
    public String actualitzarProducte(@PathVariable Long id, @ModelAttribute Producte producte,
            RedirectAttributes redirectAttributes) {
        Optional<Producte> optionalProducte = producteServei.obtenirPerId(id);
        if (optionalProducte.isPresent()) {
            Producte producteExistent = optionalProducte.get();
            producteExistent.setNom_producte(producte.getNom_producte());
            producteExistent.setDescripcio(producte.getDescripcio());
            producteExistent.setPreu(producte.getPreu());
            producteExistent.setStcok_actual(producte.getStcok_actual());
            producteExistent.setUnitat_mesura(producte.getUnitat_mesura());
            producteServei.guardar(producteExistent);
            redirectAttributes.addFlashAttribute("successMessage", "El producte s'ha actualitzat correctament.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El producte no existeix.");
        }
        return "redirect:/productes";
    }

    /**
     * Elimina un producte amb l'identificador proporcionat.
     *
     * @param id                 El identificador del producte a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges a la vista.
     * @return Una cadena que redirigeix a la pàgina de llistat de productes.
     * @throws ProducteNotFoundException Si el producte no es troba.
     * @throws Exception                 Si hi ha un error durant l'eliminació del
     *                                   producte.
     */
    @GetMapping("/productes/eliminar/{id}")
    public String eliminarProducte(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            producteServei.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "El producte s'ha eliminat correctament.");
        } catch (ProducteNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hi ha hagut un error al eliminar el producte.");
        }
        return "redirect:/productes";
    }

    /**
     * Gestiona la petició GET per mostrar les categories de productes.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra el llistat de categories
     */
    @GetMapping("/productes/categories")
    public String mostrarCategories(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<CategoriaProducte> categoria;
        if (filtro != null && valor != null) {
            categoria = categoriaServei.filtrarYOrdenar(filtro, valor, orden);
        } else {
            categoria = categoriaServei.obtenirTotes();
        }

        model.addAttribute("categories", categoria);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "inventari/categoriaLlistat";
    }

    /**
     * Crea una nova categoria de producte.
     *
     * @param categoria          L'objecte CategoriaProducte que conté la informació
     *                           de la nova categoria.
     * @param model              L'objecte Model utilitzat per passar dades a la
     *                           vista.
     * @param redirectAttributes L'objecte RedirectAttributes utilitzat per passar
     *                           missatges flash després de la redirecció.
     * @return Una cadena que representa la redirecció a la pàgina de categories de
     *         productes.
     */
    @PostMapping("/productes/categories/nou")
    public String crearCategoria(@ModelAttribute("categoria") CategoriaProducte categoria, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            categoriaServei.guardar(categoria);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría creada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al crear la categoría: " + e.getMessage());
        }
        return "redirect:/productes/categories";
    }

    /**
     * Mostra el formulari per editar una categoria de producte.
     *
     * @param id                 El identificador de la categoria a editar.
     * @param model              L'objecte Model per passar dades a la vista.
     * @param redirectAttributes L'objecte RedirectAttributes per passar missatges
     *                           flash.
     * @return El nom de la vista per editar la categoria si es troba, en cas
     *         contrari redirigeix a la llista de categories.
     */
    @GetMapping("/productes/categories/editar/{id}")
    public String mostrarFormulariEditarCategoria(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<CategoriaProducte> optionalCategoria = categoriaServei.obtenirPerId(id);
        if (optionalCategoria.isPresent()) {
            model.addAttribute("categoria", optionalCategoria.get());
            return "inventari/editarCategoriaFragment";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada.");
        return "redirect:/productes/categories";
    }

    /**
     * Actualitza una categoria de producte existent.
     *
     * @param id                 El identificador de la categoria a actualitzar.
     * @param categoriaProducte  L'objecte CategoriaProducte amb les dades
     *                           actualitzades.
     * @param redirectAttributes Els atributs de redirecció per passar missatges a
     *                           la vista.
     * @return Una cadena que indica la redirecció a la vista de categories de
     *         productes.
     */
    @PostMapping("/productes/categories/editar/{id}")
    public String actualitzarCategoria(@PathVariable Long id, @ModelAttribute CategoriaProducte categoriaProducte,
            RedirectAttributes redirectAttributes) {
        Optional<CategoriaProducte> optionalCategoria = categoriaServei.obtenirPerId(id);
        if (optionalCategoria.isPresent()) {
            CategoriaProducte categoriaExistent = optionalCategoria.get();
            categoriaExistent.setNom_categoria(categoriaProducte.getNom_categoria());
            categoriaExistent.setDescripcio(categoriaProducte.getDescripcio());
            categoriaServei.guardar(categoriaExistent);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Hubo un error al actualizar la categoría.");
        }
        return "redirect:/productes/categories";
    }

    /**
     * Elimina una categoria basada en el seu identificador.
     *
     * @param id                 El identificador de la categoria a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges de feedback a la
     *                           vista.
     * @return Una cadena que redirigeix a la pàgina de categories de productes.
     */
    @GetMapping("/productes/categories/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaServei.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hubo un error al eliminar la categoría: ");
        }
        return "redirect:/productes/categories";
    }

    /**
     * Gestiona la petició GET per mostrar l'historial de moviments dels productes.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostrarà l'historial de moviments
     */
    @GetMapping("/productes/hitorialMoviments")
    public String mostrarHistorialMoviments(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<MovimentInventari> movimentInventari;
        if (filtro != null && valor != null) {
            movimentInventari = movimentInventariServei.filtrarYOrdenarHistorial(filtro, valor, orden);
        } else {
            movimentInventari = movimentInventariServei.obtenirTots();
        }

        model.addAttribute("historialMoviments", movimentInventari);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);
        
        return "inventari/historialMoviments";
    }

    /**
     * Gestiona la petició GET per mostrar les entrades de productes.
     *
     * @param model          L'objecte Model utilitzat per passar dades a la vista.
     * @param authentication L'objecte Authentication que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista que mostra el llistat d'entrades de productes.
     */
    @GetMapping("/productes/entrades")
    public String mostrarEntrades(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<MovimentInventari> movimentInventari;
        if (filtro != null && valor != null) {
            movimentInventari = movimentInventariServei.filtrarYOrdenarEntrades(filtro, valor, orden);
        } else {
            movimentInventari = movimentInventariServei.ObtenirMovimentsEntrada();
        }

        model.addAttribute("historialMoviments", movimentInventari);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "inventari/entradesLlistat";
    }

    /**
     * Elimina una entrada de producte del moviment d'inventari.
     *
     * @param id El identificador de l'entrada de producte a eliminar.
     * @return Una redirecció a la llista d'entrades de productes.
     */
    @GetMapping("/productes/entrades/eliminar/{id}")
    public String eliminarEntrada(@PathVariable Long id) {
        movimentInventariServei.eliminar(id);
        return "redirect:/productes/entradesLlistat";
    }

    /**
     * Gestiona la petició GET per mostrar la llista de sortides de productes.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté informació sobre
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra la llista de sortides de productes
     */
    @GetMapping("/productes/sortides")
    public String mostrarSortides(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<MovimentInventari> movimentInventari;
        if (filtro != null && valor != null) {
            movimentInventari = movimentInventariServei.filtrarYOrdenarSortidas(filtro, valor, orden);
        } else {
            movimentInventari = movimentInventariServei.ObtenirMovimentsSortida();
        }

        model.addAttribute("historialMoviments", movimentInventari);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "inventari/sortidesLlistat";
    }

    /**
     * Elimina una sortida de producte del sistema.
     *
     * @param id El identificador de la sortida a eliminar.
     * @return Una redirecció a la llista de sortides de productes.
     */
    @GetMapping("/productes/sortides/eliminar/{id}")
    public String eliminarSortida(@PathVariable Long id) {
        movimentInventariServei.eliminar(id);
        return "redirect:/productes/sortidesLlistat";
    }
}
