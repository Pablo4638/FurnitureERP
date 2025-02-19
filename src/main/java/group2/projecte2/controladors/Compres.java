package group2.projecte2.controladors;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
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

import group2.projecte2.model.DetallOrdreCompra;
import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.OrdreCompra;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;
import group2.projecte2.model.Enums.EstatCompraVenta;
import group2.projecte2.model.Enums.TipusMoviment;
import group2.projecte2.serveis.DetallOrdreCompraServei;
import group2.projecte2.serveis.MovimentInventariServei;
import group2.projecte2.serveis.OrdreCompraServei;
import group2.projecte2.serveis.ProducteServei;
import group2.projecte2.serveis.ProveidorServei;
import group2.projecte2.serveis.implementacio.ProducteServeiImpl.ProducteNotFoundException;

@Controller
public class Compres {

    @Autowired
    private OrdreCompraServei ordreCompraServei;

    @Autowired
    private DetallOrdreCompraServei detallOrdreCompraServei;

    @Autowired
    private MovimentInventariServei movimentInventariServei;

    @Autowired
    private ProducteServei producteServei;

    @Autowired
    private ProveidorServei proveidorServei;

    /**
     * Gestiona les peticions GET per a la ruta "/proveidors".
     * 
     * Aquest mètode llista tots els proveïdors disponibles i els afegeix al model
     * per a ser mostrats a la vista "compres/proveidorsLlistat". També afegeix el
     * nom de l'usuari autenticat al model.
     * 
     * @param model          L'objecte Model utilitzat per passar dades a la vista.
     * @param id             L'identificador del proveïdor (no utilitzat en aquest
     *                       mètode).
     * @param authentication L'objecte Authentication que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista que ha de ser renderitzada.
     */
    @GetMapping("/proveidors")
    public String listarProveidors(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        // Obtener proveedores con filtros
        List<Proveidor> proveidors;
        if (filtro != null && valor != null) {
            proveidors = proveidorServei.filtrarYOrdenar(filtro, valor, orden);
        } else {
            proveidors = proveidorServei.obtenirTots();
        }

        model.addAttribute("proveidors", proveidors);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "compres/proveidorsLlistat";
    }

    /**
     * Crea un nou proveïdor i el guarda al servei de proveïdors.
     *
     * @param proveidor l'objecte Proveidor que es vol crear i guardar.
     * @return una cadena que redirigeix a la pàgina de llistat de proveïdors.
     */
    @PostMapping("/proveidors/nou")
    public String crearProveidor(@ModelAttribute("proveidor") Proveidor proveidor,
            RedirectAttributes redirectAttributes) {
        try {
            proveidorServei.guardar(proveidor);
            redirectAttributes.addFlashAttribute("successMessage", "Proveedor creado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el proveedor: " + e.getMessage());
        }
        return "redirect:/proveidors";
    }

    /**
     * Mostra el formulari per editar un proveïdor existent.
     *
     * @param id    El identificador del proveïdor a editar.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista per editar el proveïdor si es troba,
     *         en cas contrari redirigeix a la llista de proveïdors.
     */
    @GetMapping("/proveidors/editar/{id}")
    public String mostrarFormulariEditarProveidor(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Proveidor> optionalProveidor = proveidorServei.obtenirPerId(id);
        if (optionalProveidor.isPresent()) {
            model.addAttribute("proveidor", optionalProveidor.get());
            return "compres/editarProveidorFragment";
        } else {
            // Mensaje de error en caso de proveedor no encontrado
            redirectAttributes.addFlashAttribute("errorMessage", "Proveedor con ID " + id + " no encontrado.");
            return "redirect:/proveidors";
        }
    }

    /**
     * Actualitza un proveïdor existent amb les dades proporcionades.
     *
     * @param id                 El identificador del proveïdor a actualitzar.
     * @param proveidor          L'objecte Proveidor amb les noves dades.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return Una cadena que indica la redirecció a la pàgina de proveïdors.
     */
    @PostMapping("/proveidors/editar/{id}")
    public String actualitzarProveidor(@PathVariable Long id, @ModelAttribute Proveidor proveidor,
            RedirectAttributes redirectAttributes) {
        Optional<Proveidor> proveidorExistentOpt = proveidorServei.obtenirPerId(id);
        if (proveidorExistentOpt.isPresent()) {
            Proveidor proveidorExistent = proveidorExistentOpt.get();

            proveidorExistent.setNom(proveidor.getNom());
            proveidorExistent.setEmail(proveidor.getEmail());
            proveidorExistent.setTelefon(proveidor.getTelefon());
            proveidorExistent.setDireccio(proveidor.getDireccio());
            proveidorServei.guardar(proveidorExistent);

            redirectAttributes.addFlashAttribute("successMessage", "Proveedor actualizado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Proveedor con ID " + id + " no encontrado.");
        }
        return "redirect:/proveidors";
    }

    /**
     * Elimina un proveïdor basat en el seu identificador.
     *
     * @param id                 El identificador del proveïdor a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges flash.
     * @return Una cadena que redirigeix a la pàgina de llista de proveïdors.
     */
    @GetMapping("/proveidors/eliminar/{id}")
    public String eliminarProveidor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proveidorServei.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "El proveidor s'ha eliminat correctament.");
        } catch (ProducteNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hi ha hagut un error al eliminar el proveidor.");
        }
        return "redirect:/proveidors";
    }

    /**
     * Gestiona la petició GET per mostrar la pàgina de llistat d'ordres de compra.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista per mostrar el llistat d'ordres de compra
     */
    @GetMapping("/ordreDeCompra")
    public String mostrarOrdreCompra(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<OrdreCompra> ordreCompra;

        if (filtro != null && valor != null) {
            if (isValidFiltro(filtro)) {
                ordreCompra = ordreCompraServei.filtrarYOrdenar(filtro, valor, orden);
            } else {
                ordreCompra = ordreCompraServei.filtrarYOrdenar(null, null, orden);
            }
        } else {
            ordreCompra = ordreCompraServei.filtrarYOrdenar(null, null, orden);
        }

        model.addAttribute("ordreCompres", ordreCompra);
        model.addAttribute("proveidors", proveidorServei.obtenirTots());
        model.addAttribute("estatComanda", EstatCompraVenta.values());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "compres/ordreCompraLlistat";
    }

    private boolean isValidFiltro(String filtro) {
        return filtro != null && (filtro.equals("proveidor") || filtro.equals("dataOrdre") || filtro.equals("total"));
    }

    /**
     * Gestiona la sol·licitud GET per a la creació d'una nova ordre de compra.
     * 
     * @param model          l'objecte Model utilitzat per afegir atributs a la
     *                       vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista per seleccionar un proveïdor
     */
    @GetMapping("/ordresCompra/nova")
    public String seleccionarProveidor(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        List<Proveidor> proveidors = proveidorServei.obtenirTots();
        model.addAttribute("proveidors", proveidors);
        return "compres/seleccionarProveidor";
    }

    /**
     * Llista els productes d'un proveïdor específic.
     *
     * @param idProveidor    El identificador del proveïdor.
     * @param model          L'objecte Model per passar dades a la vista.
     * @param authentication L'objecte Authentication per obtenir informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista per mostrar els productes del proveïdor o
     *         redirigeix a la pàgina de nova ordre de compra si el proveïdor no
     *         existeix.
     */
    @GetMapping("/ordresCompra/productes/{idProveidor}")
    public String llistarProductes(@PathVariable Long idProveidor, Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        Optional<Proveidor> proveidor = proveidorServei.obtenirPerId(idProveidor);
        if (proveidor.isPresent()) {
            List<Producte> productes = producteServei.obtenirProductesPerProveeidor(proveidor.get());
            model.addAttribute("productes", productes);
            model.addAttribute("proveidor", proveidor.get());
            return "compres/productesProveidor";
        } else {
            return "redirect:/ordresCompra/nova";
        }
    }

    /**
     * Crea una nova ordre de compra.
     *
     * @param idProveidor l'identificador del proveïdor
     * @param idProductes la llista d'identificadors dels productes
     * @param quantitats  la llista de quantitats per a cada producte
     * @return una redirecció a la pàgina de creació d'ordres de compra si el
     *         proveïdor no existeix,
     *         en cas contrari redirigeix a la pàgina de nova ordre de compra
     */
    @PostMapping("/ordresCompra/crear")
    public String crearOrdre(@RequestParam Long idProveidor,
            @RequestParam(required = false) List<Long> idProductes,
            @RequestParam(required = false) List<Integer> quantitats,
            RedirectAttributes redirectAttributes) {
        Optional<Proveidor> proveidor = proveidorServei.obtenirPerId(idProveidor);
        if (proveidor.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El proveïdor no existeix.");
            return "redirect:/ordresCompra/nova";
        }

        if (idProductes == null || idProductes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El proveïdor no té productes per afegir.");
            return "redirect:/ordresCompra/nova";
        }

        OrdreCompra ordreCompra = new OrdreCompra();
        ordreCompra.setProveidor(proveidor.get());

        LocalDate currentDate = LocalDate.now();
        Date dataF = Date.valueOf(currentDate);

        ordreCompra.setData_ordre(dataF);
        ordreCompra.setEstat(EstatCompraVenta.Pendent);

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < idProductes.size(); i++) {
            int quantitat = quantitats.get(i);
            if (quantitat > 0) {
                ordreCompraServei.guardar(ordreCompra);
                Optional<Producte> producte = producteServei.obtenirPerId(idProductes.get(i));
                if (producte.isPresent()) {
                    Producte prod = producte.get();
                    BigDecimal subtotal = prod.getPreu().multiply(BigDecimal.valueOf(quantitat));

                    DetallOrdreCompra detall = new DetallOrdreCompra();
                    detall.setOrdreCompra(ordreCompra);
                    detall.setProducte(prod);
                    detall.setQuantitat(quantitat);
                    detall.setPreu_unitari(prod.getPreu());
                    detall.setSubtotal(subtotal);

                    detallOrdreCompraServei.guardar(detall);
                    total = total.add(subtotal);
                }
            }
        }

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            ordreCompra.setTotal(total);
            ordreCompraServei.guardar(ordreCompra);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No s'han afegit productes amb quantitat major a 0.");
            return "redirect:/ordresCompra/nova";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Ordre de compra creada correctament.");
        return "redirect:/ordreDeCompra";
    }

    /**
     * Mostra el formulari per editar una ordre de compra existent.
     *
     * @param id    El identificador de l'ordre de compra a editar.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista que conté el formulari d'edició de l'ordre de
     *         compra.
     */
    @GetMapping("/ordreDeCompra/editar/{id}")
    public String mostrarFormulariEditarordreDeCompra(@PathVariable Long id, Model model,
            @ModelAttribute("successMessage") String successMessage,
            @ModelAttribute("errorMessage") String errorMessage) {
        Optional<OrdreCompra> optionalOrdreCompra = ordreCompraServei.obtenirPerId(id);
        if (optionalOrdreCompra.isPresent()) {
            model.addAttribute("ordreCompra", optionalOrdreCompra.get());
            model.addAttribute("proveidors", proveidorServei.obtenirTots());
            model.addAttribute("estatComanda", EstatCompraVenta.values());
        } else {
            model.addAttribute("errorMessage", "Orden de compra con ID " + id + " no encontrada.");
        }

        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }

        if (errorMessage != null && !errorMessage.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "compres/editarCompraFragment";
    }

    @PostMapping("/ordreDeCompra/editar/{id}")
    public String actualitzarOrdreCompra(@PathVariable Long id, @ModelAttribute OrdreCompra ordrecompra,
            RedirectAttributes redirectAttributes) {
        Optional<OrdreCompra> ordreCompraExistentOpt = ordreCompraServei.obtenirPerId(id);
        if (ordreCompraExistentOpt.isPresent()) {
            OrdreCompra ordreCompraExistent = ordreCompraExistentOpt.get();

            ordreCompraExistent.setEstat(ordrecompra.getEstat());
            ordreCompraServei.guardar(ordreCompraExistent);

            if (ordrecompra.getEstat() == EstatCompraVenta.Completada) {
                List<DetallOrdreCompra> detalls = detallOrdreCompraServei.obtenirPerOrdreCompra(ordreCompraExistent);
                for (DetallOrdreCompra detall : detalls) {
                    Producte producte = detall.getProducte();

                    BigDecimal nouStock = producte.getStcok_actual().add(BigDecimal.valueOf(detall.getQuantitat()));
                    producte.setStcok_actual(nouStock);
                    producteServei.guardar(producte);

                    MovimentInventari moviment = new MovimentInventari();
                    moviment.setProducte(producte);
                    moviment.setQuanttitat(detall.getQuantitat());
                    moviment.setTipus_moviment(TipusMoviment.Entrada); // Enum para indicar que es una entrada
                    moviment.setData_moviment(Date.valueOf(LocalDate.now()));
                    movimentInventariServei.guardar(moviment);
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Orden de compra actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Orden de compra con ID " + id + " no encontrada.");
        }

        return "redirect:/ordreDeCompra";
    }

    /**
     * Elimina una ordre de compra identificada pel seu ID.
     *
     * @param id El ID de l'ordre de compra que es vol eliminar.
     * @return Una redirecció a la pàgina de llistat d'ordres de compra.
     */
    @GetMapping("/ordreDeCompra/eliminar/{id}")
    public String eliminarOrdreCompra(@PathVariable Long id) {
        ordreCompraServei.eliminar(id);
        return "redirect:/ordreDeCompra";
    }

    /**
     * Mostra l'historial de comandes de l'usuari autenticat.
     *
     * @param model          el model utilitzat per afegir atributs a la vista
     * @param authentication l'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra l'historial de comandes
     */
    @GetMapping("/ordreCompraHistorial")
    public String mostrarHistorialComandes(
            @RequestParam(value = "filtro", required = false) String filtro,
            @RequestParam(value = "valor", required = false) String valor,
            @RequestParam(value = "orden", required = false) String orden,
            Model model,
            Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<OrdreCompra> ordenat = ordreCompraServei.filtrarYOrdenarHistorial(filtro, valor, orden);

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "compres/ordreCompraHistorial";
    }

    /**
     * Mostra els detalls d'una ordre de compra específica.
     *
     * @param id    El identificador de l'ordre de compra.
     * @param model El model utilitzat per passar dades a la vista.
     * @return La vista amb els detalls de l'ordre de compra.
     */
    @GetMapping("/ordreDeCompra/{id}")
    public String mostrarOrdreCompra(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        List<DetallOrdreCompra> detalls = detallOrdreCompraServei.obtenirPerOrdreCompraId(id);
        if (!detalls.isEmpty()) {
            model.addAttribute("detalls", detalls);
        } else {
            model.addAttribute("error", "No se han encontrado detalles para esta comanda.");
        }
        return "compres/detallOrdreCompra";
    }
}