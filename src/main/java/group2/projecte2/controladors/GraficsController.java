package group2.projecte2.controladors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import group2.projecte2.serveis.FacturaServei;
import group2.projecte2.serveis.OrdreCompraServei;

@Controller
public class GraficsController {

    @Autowired
    private OrdreCompraServei ordreCompraServei;

    @Autowired
    private FacturaServei facturaServei;

    @GetMapping("/grafics")
    public String mostrarGrafics(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("nomUsuari", username);

        List<Double> ventasAnuales = facturaServei.obtenirVentasAnuales();
        List<Double> ordresAnuales = ordreCompraServei.ordresVentasAnuales();

        if (ventasAnuales != null && !ventasAnuales.isEmpty()) {
            model.addAttribute("ventasAnuales", ventasAnuales);
        } else {
            model.addAttribute("ventasAnuales", new ArrayList<Double>());
        } 

        if (ordresAnuales != null && !ordresAnuales.isEmpty()) {
            model.addAttribute("ordresAnuales", ordresAnuales);
        } else {
            model.addAttribute("ordresAnuales", new ArrayList<Double>());
        }

        return "grafics/grafics";
    }

    @GetMapping("/api/ventasAnuales")
    @ResponseBody
    public List<Double> getVentasAnuales() {
        return facturaServei.obtenirVentasAnuales();
    }

    @GetMapping("/api/ventasMensuales")
    @ResponseBody
    public List<Double> obtenerVentasMensuales(@RequestParam int year) {
        return facturaServei.obtenirVentasMensuales(year);
    }

    @GetMapping("/api/ventasSemanales")
    @ResponseBody
    public List<Double> getVentasSemanales() {
        return facturaServei.obtenirVentasSemanales();
    }

    @GetMapping("/api/ordresAnuales")
    @ResponseBody
    public List<Double> getOrdresVentasAnuales() {
        return ordreCompraServei.ordresVentasAnuales();
    }
}
