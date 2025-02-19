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

import group2.projecte2.model.Client;
import group2.projecte2.model.Comanda;
import group2.projecte2.model.DetallComanda;
import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Enums.ClientTipusEnum;
import group2.projecte2.model.Enums.EstatCompraVenta;
import group2.projecte2.model.Enums.TipusMoviment;
import group2.projecte2.serveis.ClientServei;
import group2.projecte2.serveis.ComandaServei;
import group2.projecte2.serveis.DetallComandaServei;
import group2.projecte2.serveis.MovimentInventariServei;
import group2.projecte2.serveis.ProducteServei;

@Controller
public class Ventas {

    String redirectClients = "redirect:/clients";
    String redirectComandes = "redirect:/comandes";

    @Autowired
    private ClientServei clientServei;
    @Autowired
    private ComandaServei comandesServei;

    @Autowired
    private DetallComandaServei detallComandaServei;

    @Autowired
    private ProducteServei producteServei;
    @Autowired
    private MovimentInventariServei movimentInventariServei;

    /**
     * Gestiona la petició GET per mostrar la llista de clients.
     *
     * @param model          l'objecte Model utilitzat per passar dades a la vista
     * @param authentication l'objecte Authentication que conté la informació de
     *                       l'usuari autenticat
     * @return el nom de la vista que mostra la llista de clients
     */
    @GetMapping("/clients")
    public String mostrarClients(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Client> clients = clientServei.obtenirTots();
        if (filtro != null && valor != null) {
            clients = clientServei.filtrarYOrdenar(filtro, valor, orden);
        } else {
            clients = clientServei.obtenirTots();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("tipusClients", ClientTipusEnum.values());
    

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "ventas/clientsLlistat";
    }

    /**
     * Crea un nou client i el guarda al servei de clients.
     * 
     * @param client             L'objecte Client que es vol crear.
     * @param redirectAttributes Atributs per redirigir missatges de feedback a la
     *                           vista.
     * @return Una cadena que redirigeix a la pàgina de llista de clients.
     */
    @PostMapping("/clients/nou")
    public String crearClient(@ModelAttribute("client") Client client, RedirectAttributes redirectAttributes) {
        try {
            clientServei.guardar(client);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente creado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hubo un error al crear el cliente.");
        }
        return "redirect:/clients";
    }

    /**
     * Mostra el formulari per editar un client existent.
     *
     * @param id    El identificador del client a editar.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista per editar el client o una redirecció si el client
     *         no existeix.
     */
    @GetMapping("/clients/editar/{id}")
    public String mostrarFormulariEditarClient(@PathVariable Long id, Model model) {
        Optional<Client> optionalClient = clientServei.obtenirPerId(id);
        if (optionalClient.isPresent()) {
            model.addAttribute("client", optionalClient.get());
            model.addAttribute("tipusClients", ClientTipusEnum.values());
            return "ventas/editarClientFragment";
        } else {
            return redirectClients;
        }
    }

    /**
     * Actualitza un client existent amb les dades proporcionades.
     *
     * @param id                 El identificador del client a actualitzar.
     * @param client             L'objecte Client amb les noves dades.
     * @param redirectAttributes Objecte per afegir atributs de redirecció.
     * @return La redirecció a la pàgina de llistat de clients.
     */
    @PostMapping("/clients/editar/{id}")
    public String actualitzarClient(@PathVariable Long id, @ModelAttribute Client client,
            RedirectAttributes redirectAttributes) {
        Optional<Client> clientExistentOpt = clientServei.obtenirPerId(id);
        if (clientExistentOpt.isPresent()) {
            Client clientExistent = clientExistentOpt.get();
            clientExistent.setNom(client.getNom());
            clientExistent.setEmail(client.getEmail());
            clientExistent.setTelefon(client.getTelefon());
            clientExistent.setDireccio(client.getDireccio());
            clientExistent.setTipus_client(client.getTipus_client());
            clientServei.guardar(clientExistent);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el cliente para actualizar.");
        }
        return "redirect:/clients";
    }

    /**
     * Elimina un client basat en el seu identificador.
     *
     * @param id                 El identificador del client a eliminar.
     * @param redirectAttributes Atributs per redirigir missatges de feedback a la
     *                           vista.
     * @return Una cadena que redirigeix a la pàgina de llistat de clients.
     */
    @GetMapping("/clients/eliminar/{id}")
    public String eliminarClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Client> clientOpt = clientServei.obtenirPerId(id);
        if (clientOpt.isPresent()) {
            try {
                clientServei.eliminar(id);
                redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar el cliente.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el cliente para eliminar.");
        }
        return "redirect:/clients";
    }

    /**
     * Mètode per mostrar les comandes pendents.
     * 
     * @param model          El model utilitzat per afegir atributs a la vista.
     * @param authentication L'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista que mostra el llistat de comandes pendents.
     */
    @GetMapping("/comandes")
    public String mostrarComandes(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Comanda> comanda;
        // Si filtro y valor son no nulos, realiza el filtrado y ordenación
        if (filtro != null && valor != null) {
            if (isValidFiltro(filtro)) {
                comanda = comandesServei.filtrarYOrdenar(filtro, valor, orden);
            } else {
                comanda = comandesServei.filtrarYOrdenar(null, null, orden); // Solo estado "pendent"
            }
        } else {
            comanda = comandesServei.filtrarYOrdenar(null, null, orden); // Solo estado "pendent"
        }

        model.addAttribute("comandes", comanda);
        model.addAttribute("clients", clientServei.obtenirTots());
        model.addAttribute("estats", EstatCompraVenta.values());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "ventas/comandesLlistat";
    }

    /**
     * Gestiona la petició GET per mostrar el formulari per crear una nova comanda.
     *
     * @param model          El model utilitzat per passar dades a la vista.
     * @param authentication L'objecte d'autenticació que conté informació sobre
     *                       l'usuari autenticat.
     * @return El nom de la vista que ha de ser renderitzada.
     */
    // Método para verificar si el filtro es válido
    private boolean isValidFiltro(String filtro) {
        return filtro != null && (filtro.equals("client") || filtro.equals("dataComanda") || filtro.equals("total"));
    }

    @GetMapping("/comandes/nou")
    public String mostrarFormulariNouComanda(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        model.addAttribute("comanda", new Comanda());
        model.addAttribute("clients", clientServei.obtenirTots());
        model.addAttribute("productes", producteServei.obtenirTots());
        return "ventas/nou_comanda";
    }

    /**
     * Crea una nova comanda amb els productes seleccionats.
     *
     * @param comanda            L'objecte Comanda que es crearà.
     * @param clientId           L'identificador del client que realitza la comanda.
     * @param productIds         La llista d'identificadors dels productes
     *                           seleccionats.
     * @param cantidades         La llista de quantitats corresponents als productes
     *                           seleccionats.
     * @param redirectAttributes Els atributs per redirigir missatges d'error o
     *                           èxit.
     * @return La redirecció a la pàgina de creació de comandes en cas d'error, o a
     *         la pàgina de comandes en cas d'èxit.
     */
    @PostMapping("/comandes/nou")
    public String crearComanda(
            @ModelAttribute Comanda comanda,
            @RequestParam Long clientId,
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> cantidades,
            RedirectAttributes redirectAttributes) {

        // Validar si las cantidades son todas cero
        boolean todasCantidadesCero = cantidades.stream().allMatch(cantidad -> cantidad == 0);
        if (todasCantidadesCero) {
            redirectAttributes.addFlashAttribute("error", "No se puede crear una comanda sin productos seleccionados.");
            return "redirect:/comandes/nou";
        }

        Client cliente = clientServei.obtenirPerId(clientId).orElse(null);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/comandes/nou"; // Redirige con error
        }
        comanda.setClient(cliente);
        comanda.setEstat(EstatCompraVenta.Pendent);

        BigDecimal totalComanda = BigDecimal.ZERO;

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer cantidad = cantidades.get(i);

            if (cantidad == 0) {
                continue;
            }

            Producte producto = producteServei.obtenirPerId(productId).orElse(null);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto con ID " + productId + " no encontrado.");
                return "redirect:/comandes/nou"; // Redirige con error
            }

            BigDecimal stockActual = producto.getStcok_actual();
            if (stockActual.compareTo(new BigDecimal(cantidad)) < 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Stock insuficiente para el producto: " + producto.getNom_producte());
                return "redirect:/comandes/nou"; // Redirige con error
            }

            // Calcular subtotal para el producto
            BigDecimal subtotal = producto.getPreu().multiply(new BigDecimal(cantidad));
            totalComanda = totalComanda.add(subtotal);
        }

        // Si pasa todas las validaciones, proceder con la lógica de guardado
        comanda.setTotal(totalComanda);
        comanda.setData_comanda(Date.valueOf(LocalDate.now()));
        comandesServei.guardar(comanda);

        // Guardar los detalles de la comanda y actualizar stock
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer cantidad = cantidades.get(i);

            if (cantidad == 0) {
                continue;
            }

            Producte producto = producteServei.obtenirPerId(productId).get();
            BigDecimal stockActual = producto.getStcok_actual();

            DetallComanda detallComanda = new DetallComanda();
            detallComanda.setComanda(comanda);
            detallComanda.setProducte(producto);
            detallComanda.setQuantitat(cantidad);

            // Calcular subtotal y asignarlo
            BigDecimal subtotal = producto.getPreu().multiply(new BigDecimal(cantidad));
            detallComanda.setSubtotal(subtotal);

            // Actualizar stock
            producto.setStcok_actual(stockActual.subtract(new BigDecimal(cantidad)));
            producteServei.guardar(producto);

            // Guardar el detalle de la comanda
            detallComandaServei.guardar(detallComanda);
        }

        redirectAttributes.addFlashAttribute("success", "Comanda creada exitosamente.");
        return redirectComandes; // Redirige a la lista de comandas
    }

    /**
     * Mostra el formulari per editar una comanda existent.
     *
     * @param id    L'identificador de la comanda a editar.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista a mostrar.
     */
    @GetMapping("/comandes/editar/{id}")
    public String mostrarFormulariEditarComanda(@PathVariable Long id, Model model) {
        Optional<Comanda> optionalComanda = comandesServei.obtenirPerId(id);
        if (optionalComanda.isPresent()) {
            model.addAttribute("comanda", optionalComanda.get());
            model.addAttribute("estats", EstatCompraVenta.values());
            return "ventas/editarComandaFragment";
        } else {
            model.addAttribute("errorMessage", "No se encontró la comanda para editar.");
            return "ventas/comandes";
        }
    }

    /**
     * Actualitza una comanda existent amb l'ID proporcionat.
     *
     * @param id                 El ID de la comanda a actualitzar.
     * @param comanda            L'objecte Comanda amb les dades actualitzades.
     * @param redirectAttributes Atributs per a redirecció per mostrar missatges de
     *                           feedback.
     * @return Una cadena que indica la redirecció a la pàgina de comandes.
     */
    @PostMapping("/comandes/editar/{id}")
    public String actualitzarComanda(@PathVariable Long id, @ModelAttribute Comanda comanda,
            RedirectAttributes redirectAttributes) {
        Optional<Comanda> comandaExistentOpt = comandesServei.obtenirPerId(id);

        if (comandaExistentOpt.isPresent()) {
            Comanda comandaExistent = comandaExistentOpt.get();

            if (comanda.getEstat() == EstatCompraVenta.Cancelada
                    && comandaExistent.getEstat() != EstatCompraVenta.Cancelada) {
                List<DetallComanda> detalls = detallComandaServei.obtenirPerComanda(id);
                for (DetallComanda detall : detalls) {
                    Producte producte = detall.getProducte();
                    BigDecimal stockActual = producte.getStcok_actual();
                    BigDecimal cantidadRestaurada = new BigDecimal(detall.getQuantitat());
                    producte.setStcok_actual(stockActual.add(cantidadRestaurada));
                    producteServei.guardar(producte);
                }
            }

            if (comanda.getEstat() == EstatCompraVenta.Completada) {
                List<DetallComanda> detalls = detallComandaServei.obtenirPerComanda(comandaExistent.getId_comanda());
                for (DetallComanda detall : detalls) {
                    Producte producte = detall.getProducte();

                    MovimentInventari moviment = new MovimentInventari();
                    moviment.setProducte(producte);
                    moviment.setQuanttitat(detall.getQuantitat());
                    moviment.setTipus_moviment(TipusMoviment.Sortida);
                    moviment.setData_moviment(Date.valueOf(LocalDate.now()));
                    movimentInventariServei.guardar(moviment);
                }
            }

            comandaExistent.setEstat(comanda.getEstat());
            comandesServei.guardar(comandaExistent);

            redirectAttributes.addFlashAttribute("successMessage", "Comanda actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo encontrar la comanda.");
        }

        return "redirect:/comandes";
    }

    /**
     * Gestiona la petició GET per a l'historial de comandes.
     * 
     * Afegeix al model les comandes completades i cancel·lades, tots els clients,
     * els estats de compra/venda i el nom d'usuari autenticat.
     * 
     * @param model          El model utilitzat per a afegir atributs a la vista.
     * @param authentication L'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista per a l'historial de comandes.
     */
    @GetMapping("/historialComandes")
    public String historialComandes(
            Model model,
            @RequestParam(value = "filtro", required = false) String filtro,
            @RequestParam(value = "valor", required = false) String valor,
            @RequestParam(value = "orden", required = false) String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Comanda> ordenat = comandesServei.filtrarYOrdenarHistorial(filtro, valor, orden);

        model.addAttribute("comandes", ordenat);
        model.addAttribute("clients", clientServei.obtenirTots());
        model.addAttribute("estats", EstatCompraVenta.values());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);

        return "ventas/hisotrialComandes";
    }

    /**
     * Gestiona la petició GET per mostrar els detalls d'una comanda específica.
     *
     * @param id    L'identificador de la comanda de la qual es volen obtenir els
     *              detalls.
     * @param model L'objecte Model utilitzat per passar dades a la vista.
     * @return El nom de la vista a mostrar. Si es troben detalls per a la comanda,
     *         es retorna la vista "ventas/detallComandaLlistat". Si no es troben
     *         detalls, es retorna la vista "ventas/error" amb un missatge d'error.
     */
    @GetMapping("/detallComanda/{id}")
    public String mostrarDetallComanda(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        List<DetallComanda> detalls = detallComandaServei.obtenirPerComanda(id);
        if (!detalls.isEmpty()) {
            model.addAttribute("detalls", detalls);
        } else {
            model.addAttribute("error", "No se han encontrado detalles para esta comanda.");
            return "ventas/error";
        }
        return "ventas/detallComandaLlistat";
    }
}