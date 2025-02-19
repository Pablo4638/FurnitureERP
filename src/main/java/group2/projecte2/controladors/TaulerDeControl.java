package group2.projecte2.controladors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import group2.projecte2.model.Client;
import group2.projecte2.model.OrdreCompra;
import group2.projecte2.serveis.ClientServei;
import group2.projecte2.serveis.ComandaServei;
import group2.projecte2.serveis.EmpleatServei;
import group2.projecte2.serveis.OrdreCompraServei;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class TaulerDeControl {

    @Autowired
    private OrdreCompraServei ordreCompraServei;
    @Autowired
    private ClientServei clientServei;
    @Autowired
    private EmpleatServei empleatServei;
    @Autowired
    private ComandaServei comandaServei;

    /**
     * Gestiona les redireccions a diferents taulers de control segons el rol de
     * l'usuari autenticat.
     *
     * @param response       l'objecte HttpServletResponse utilitzat per enviar
     *                       redireccions.
     * @param authentication l'objecte Authentication que conté la informació
     *                       d'autenticació de l'usuari.
     * @throws IOException si hi ha un error en enviar la redirecció.
     */
    @GetMapping("/taulerDeControl")
    public void taulerDeControl(HttpServletResponse response, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/taulerDeControlAdmin");
        } else if (roles.contains("ROLE_RRHH")) {
            response.sendRedirect("/taulerDeControlRRHH");
        } else if (roles.contains("ROLE_FINANCES")) {
            response.sendRedirect("taulerDeControlFINANCES");
        } else if (roles.contains("ROLE_INVENTARI")) {
            response.sendRedirect("taulerDeControlINVENTARI");
        } else {
            response.sendRedirect("/taulerDeControlUser");
        }
    }

    /**
     * Mostra el tauler de control per a l'administrador.
     *
     * @param model          El model utilitzat per afegir atributs a la vista.
     * @param authentication L'objecte d'autenticació que conté informació sobre
     *                       l'usuari autenticat.
     * @return La vista del tauler de control de l'administrador.
     */
    @GetMapping("/taulerDeControlAdmin")
    public String mostrarTaulerAdmin(Model model, Authentication authentication) {
        List<OrdreCompra> ordenat = ordreCompraServei.BuscarOrdresCompletades();
        ordenat = ordreCompraServei.ordenarPerData(ordenat);
        if (ordenat.size() > 15) {
            ordenat = ordenat.subList(1, 14);
        }

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("PercentatgeDeCanvi", comandaServei.PercentatgeDeCanvi());

        try {
            Client ClientQueMesGasta = clientServei.buscarClientQueMesGasta();
            model.addAttribute("ClientQueMesGasta", ClientQueMesGasta);

            Long idClient = ClientQueMesGasta.getId_client();
            BigDecimal totalGastat = clientServei.totalGastatPerClient(idClient);
            model.addAttribute("totalGastat", totalGastat);
            double calcularPorcentajeGasto = comandaServei.calcularPorcentajeGastoCliente(idClient);
            model.addAttribute("calcularPorcentajeGasto", calcularPorcentajeGasto);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "No se encontraron clientes con gastos registrados.");
        }

        BigDecimal comandasTotal = comandaServei.totalComandasAvui();
        model.addAttribute("comandasTotal", comandasTotal);

        Long totalEmpleats = empleatServei.recompte();
        model.addAttribute("totalEmpleats", totalEmpleats);

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        return "/taulerDeControl/admin";
    }

    /**
     * Mostra el tauler de control per a l'usuari.
     *
     * @param model          el model per afegir atributs
     * @param authentication l'autenticació de l'usuari actual
     * @return la vista del tauler de control de l'usuari
     */
    @GetMapping("/taulerDeControlUser")
    public String mostrarTaulerUser(Model model, Authentication authentication) {
        List<OrdreCompra> ordenat = ordreCompraServei.BuscarOrdresCompletades();
        ordenat = ordreCompraServei.ordenarPerData(ordenat);
        if (ordenat.size() > 15) {
            ordenat = ordenat.subList(1, 14);
        }

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("PercentatgeDeCanvi", comandaServei.PercentatgeDeCanvi());

        try {
            Client ClientQueMesGasta = clientServei.buscarClientQueMesGasta();
            model.addAttribute("ClientQueMesGasta", ClientQueMesGasta);

            Long idClient = ClientQueMesGasta.getId_client();
            BigDecimal totalGastat = clientServei.totalGastatPerClient(idClient);
            model.addAttribute("totalGastat", totalGastat);
            double calcularPorcentajeGasto = comandaServei.calcularPorcentajeGastoCliente(idClient);
            model.addAttribute("calcularPorcentajeGasto", calcularPorcentajeGasto);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "No se encontraron clientes con gastos registrados.");
        }

        BigDecimal comandasTotal = comandaServei.totalComandasAvui();
        model.addAttribute("comandasTotal", comandasTotal);

        Long totalEmpleats = empleatServei.recompte();
        model.addAttribute("totalEmpleats", totalEmpleats);

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        return "/taulerDeControl/user";
    }

    /**
     * Mètode per mostrar el tauler de control de RRHH.
     *
     * @param model          El model utilitzat per afegir atributs a la vista.
     * @param authentication L'objecte d'autenticació que conté informació de
     *                       l'usuari autenticat.
     * @return La vista del tauler de control de RRHH.
     */
    @GetMapping("/taulerDeControlRRHH")
    public String mostrarTaulerRrhh(Model model, Authentication authentication) {
        List<OrdreCompra> ordenat = ordreCompraServei.BuscarOrdresCompletades();
        ordenat = ordreCompraServei.ordenarPerData(ordenat);
        if (ordenat.size() > 15) {
            ordenat = ordenat.subList(1, 14);
        }

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("PercentatgeDeCanvi", comandaServei.PercentatgeDeCanvi());

        try {
            Client ClientQueMesGasta = clientServei.buscarClientQueMesGasta();
            model.addAttribute("ClientQueMesGasta", ClientQueMesGasta);

            Long idClient = ClientQueMesGasta.getId_client();
            BigDecimal totalGastat = clientServei.totalGastatPerClient(idClient);
            model.addAttribute("totalGastat", totalGastat);
            double calcularPorcentajeGasto = comandaServei.calcularPorcentajeGastoCliente(idClient);
            model.addAttribute("calcularPorcentajeGasto", calcularPorcentajeGasto);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "No se encontraron clientes con gastos registrados.");
        }

        BigDecimal comandasTotal = comandaServei.totalComandasAvui();
        model.addAttribute("comandasTotal", comandasTotal);

        Long totalEmpleats = empleatServei.recompte();
        model.addAttribute("totalEmpleats", totalEmpleats);

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        return "/taulerDeControl/rrhh";
    }

    /**
     * Mètode que maneja les sol·licituds GET per a la pàgina del tauler de control
     * de finances.
     * 
     * @param model          L'objecte Model utilitzat per afegir atributs a la
     *                       vista.
     * @param authentication L'objecte Authentication que conté la informació de
     *                       l'usuari autenticat.
     * @return El nom de la vista per al tauler de control de finances.
     */
    @GetMapping("/taulerDeControlFINANCES")
    public String mostrarTaulerFinances(Model model, Authentication authentication) {
        List<OrdreCompra> ordenat = ordreCompraServei.BuscarOrdresCompletades();
        ordenat = ordreCompraServei.ordenarPerData(ordenat);
        if (ordenat.size() > 15) {
            ordenat = ordenat.subList(1, 14);
        }

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("PercentatgeDeCanvi", comandaServei.PercentatgeDeCanvi());

        try {
            Client ClientQueMesGasta = clientServei.buscarClientQueMesGasta();
            model.addAttribute("ClientQueMesGasta", ClientQueMesGasta);

            Long idClient = ClientQueMesGasta.getId_client();
            BigDecimal totalGastat = clientServei.totalGastatPerClient(idClient);
            model.addAttribute("totalGastat", totalGastat);
            double calcularPorcentajeGasto = comandaServei.calcularPorcentajeGastoCliente(idClient);
            model.addAttribute("calcularPorcentajeGasto", calcularPorcentajeGasto);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "No se encontraron clientes con gastos registrados.");
        }

        BigDecimal comandasTotal = comandaServei.totalComandasAvui();
        model.addAttribute("comandasTotal", comandasTotal);

        Long totalEmpleats = empleatServei.recompte();
        model.addAttribute("totalEmpleats", totalEmpleats);

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        return "/taulerDeControl/finances";
    }

    /**
     * Mètode que maneja les sol·licituds GET per a la ruta
     * "/taulerDeControlINVENTARI".
     * Mostra el tauler de control de l'inventari amb diverses dades estadístiques.
     *
     * @param model          l'objecte Model utilitzat per afegir atributs a la
     *                       vista
     * @param authentication l'objecte Authentication que conté informació sobre
     *                       l'usuari autenticat
     * @return el nom de la vista "/taulerDeControl/inventari"
     */
    @GetMapping("/taulerDeControlINVENTARI")
    public String mostrarTaulerInventari(Model model, Authentication authentication) {
        List<OrdreCompra> ordenat = ordreCompraServei.BuscarOrdresCompletades();
        ordenat = ordreCompraServei.ordenarPerData(ordenat);
        if (ordenat.size() > 15) {
            ordenat = ordenat.subList(1, 14);
        }

        model.addAttribute("ordreCompres", ordenat);

        model.addAttribute("PercentatgeDeCanvi", comandaServei.PercentatgeDeCanvi());

        try {
            Client ClientQueMesGasta = clientServei.buscarClientQueMesGasta();
            model.addAttribute("ClientQueMesGasta", ClientQueMesGasta);

            Long idClient = ClientQueMesGasta.getId_client();
            BigDecimal totalGastat = clientServei.totalGastatPerClient(idClient);
            model.addAttribute("totalGastat", totalGastat);
            double calcularPorcentajeGasto = comandaServei.calcularPorcentajeGastoCliente(idClient);
            model.addAttribute("calcularPorcentajeGasto", calcularPorcentajeGasto);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "No se encontraron clientes con gastos registrados.");
        }

        BigDecimal comandasTotal = comandaServei.totalComandasAvui();
        model.addAttribute("comandasTotal", comandasTotal);

        Long totalEmpleats = empleatServei.recompte();
        model.addAttribute("totalEmpleats", totalEmpleats);

        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        return "/taulerDeControl/inventari";
    }
}