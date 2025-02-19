package group2.projecte2.controladors;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import group2.projecte2.model.Factura;
import group2.projecte2.model.Pagament;
import group2.projecte2.model.Client;
import group2.projecte2.model.Comanda;
import group2.projecte2.serveis.ClientServei;
import group2.projecte2.serveis.ComandaServei;
import group2.projecte2.serveis.FacturaServei;
import group2.projecte2.serveis.PagamentServei;

@Controller
public class Finances {

    @Autowired
    private FacturaServei facturaServei;

    @Autowired
    private ComandaServei comandaServei;

    @Autowired
    private ClientServei clientServei;

    @Autowired
    private PagamentServei pagamentServei;

    /**
     * Mètode per mostrar les factures a la vista.
     *
     * @param model          l'objecte Model per afegir atributs a la vista
     * @param authentication l'objecte Authentication per obtenir el nom d'usuari
     *                       autenticat
     * @return el nom de la vista per mostrar les factures
     */
    @GetMapping("/finances")
    public String mostrarFacturas(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Factura> factura;
        if (filtro != null && valor != null) {
            if (isValidFiltro(filtro)) {
                factura = facturaServei.filtrarYOrdenar(filtro, valor, orden);
            } else {
                factura = facturaServei.filtrarYOrdenar(null, null, orden);
            }
        } else {
            factura = facturaServei.filtrarYOrdenar(null, null, orden);
        }
        model.addAttribute("comandes", comandaServei.obtenirTots());
        model.addAttribute("factures", factura);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);
        return "finances/facturesLlistar";
    }

    /**
     * Endpoint per veure una factura en format PDF.
     *
     * @param id El identificador de la factura.
     * @return ResponseEntity amb el recurs de la factura en format PDF.
     * @throws RuntimeException si no es pot llegir l'arxiu o hi ha un error al
     *                          carregar la factura.
     */
    private boolean isValidFiltro(String filtro) {
        return filtro != null && (filtro.equals("nom") || filtro.equals("datafactura") || filtro.equals("estat")
                || filtro.equals("total"));
    }

    @GetMapping("/factures/{id}")
    public ResponseEntity<Resource> verFactura(@PathVariable Long id) {
        try {
            Path facturaPath = Paths.get("facturas", "Factura" + id + ".pdf");
            Resource resource = new UrlResource(facturaPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("No es pot llegir l'arxiu" + facturaPath);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error al carregar la factura", e);
        }
    }

    /**
     * Genera una factura en format PDF per a un identificador donat i la retorna
     * com a recurs.
     *
     * @param id El identificador de la factura a generar.
     * @return ResponseEntity que conté el recurs PDF de la factura.
     * @throws RuntimeException Si no es pot llegir l'arxiu de la factura o si hi ha
     *                          un error en carregar la factura.
     */
    @GetMapping("/facturesGenerar/{id}")
    public ResponseEntity<Resource> generarFactura(@PathVariable Long id) {
        facturaServei.generatePdfWithCoordinates(id);
        try {
            Path facturaPath = Paths.get("facturas", "Factura" + id + ".pdf");
            Resource resource = new UrlResource(facturaPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("No es pot llegir l'arxiu" + facturaPath);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error al carregar la factura", e);
        }
    }

    /**
     * Gestiona la petició GET per a la ruta "/factures/nova" i retorna la vista per
     * seleccionar un client.
     *
     * @param model          l'objecte Model utilitzat per afegir atributs a la
     *                       vista
     * @param authentication l'objecte Authentication que conté la informació
     *                       d'autenticació de l'usuari
     * @return el nom de la vista "finances/seleccionarClient"
     */
    @GetMapping("/factures/nova")
    public String seleccionarClient(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        model.addAttribute("clients", clientServei.obtenirTots());
        return "finances/seleccionarClient";
    }

    /**
     * Gestiona la petició GET per obtenir les comandes d'un client específic per a
     * una nova factura.
     *
     * @param id    El identificador del client.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista que mostra les comandes del client.
     */
    @GetMapping("/factures/nova/{id}")
    public String clientsPerFactura(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);
        Client client = clientServei.obtenirPerId(id).orElse(null);
        model.addAttribute("client", client);
        List<Comanda> comandes = comandaServei.obtenirComandesClient(client);
        model.addAttribute("comandes", comandes);
        return "finances/comandesClients";
    }

    /**
     * Gestiona la petició GET per a l'historial de pagaments.
     * 
     * @param model          El model utilitzat per a passar dades a la vista.
     * @param authentication L'objecte d'autenticació que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista per a mostrar l'historial de pagaments.
     */
    @GetMapping("/historialPagaments")
    public String historialPagaments(
            Model model,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Authentication authentication) {

        System.out.println(authentication.getPrincipal().toString());
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Pagament> pagament;
        if (filtro != null && valor != null) {
            if (isValidFiltroPagament(filtro)) {
                pagament = pagamentServei.filtrarYOrdenar(filtro, valor, orden);
            } else {
                pagament = pagamentServei.filtrarYOrdenar(null, null, orden);
            }
        } else {
            pagament = pagamentServei.filtrarYOrdenar(null, null, orden);
        }
        model.addAttribute("pagaments", pagament);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("orden", orden);
        return "finances/pagamentsLlistar";
    }

    private boolean isValidFiltroPagament(String filtro) {
        return filtro != null && (filtro.equals("nom") || filtro.equals("datapagament") || filtro.equals("nfactura")
                || filtro.equals("total"));
    }

}