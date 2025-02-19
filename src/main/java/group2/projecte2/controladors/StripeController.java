package group2.projecte2.controladors;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;

import group2.projecte2.model.Factura;
import group2.projecte2.model.Pagament;
import group2.projecte2.model.Enums.EstatFactura;
import group2.projecte2.serveis.CompteBancariServei;
import group2.projecte2.serveis.FacturaServei;
import group2.projecte2.serveis.PagamentServei;

@Controller
@RequestMapping("/api/stripe")
public class StripeController {

    @Autowired
    PagamentServei pagamentServei;

    @Autowired
    CompteBancariServei compteBancariServei;

    @Autowired
    FacturaServei facturaServei;

    /**
     * Gestiona la sol·licitud GET per a la pàgina de pagament.
     *
     * @param id    El identificador de la factura.
     * @param model El model utilitzat per passar dades a la vista.
     * @return El nom de la vista de pagament.
     */
    @GetMapping("/pagament/{id}")
    public String paymentPage(@PathVariable Long id, Model model) {
        Factura factura = facturaServei.obtenirPerId(id).orElse(null);
        model.addAttribute("factura", factura);
        return "payment";
    }

    /**
     * Endpoint per crear un càrrec a Stripe.
     *
     * @param chargeRequest Map amb les dades del càrrec, incloent-hi:
     *                      - amount: L'import del càrrec en cèntims.
     *                      - source: La font de pagament (token de Stripe).
     *                      - facturaId: L'identificador de la factura associada.
     * @return Un Map amb l'estat del càrrec i, si és correcte, l'identificador del
     *         càrrec i la URL de redirecció.
     */
    @PostMapping("/charge")
    @ResponseBody
    public Map<String, Object> createCharge(@RequestBody Map<String, Object> chargeRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long amount = Long.valueOf(chargeRequest.get("amount").toString());
            String source = chargeRequest.get("source").toString();
            Long facturaId = Long.valueOf(chargeRequest.get("facturaId").toString());

            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("eur")
                    .setDescription("Example charge")
                    .setSource(source)
                    .build();

            Charge charge = Charge.create(params);

            Pagament pagament = new Pagament();
            pagament.setTotal(BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100)));
            LocalDate currentDate = LocalDate.now();
            Date dataF = Date.valueOf(currentDate);
            pagament.setData_pagament(dataF);
            Factura factura = facturaServei.obtenirPerId(facturaId).orElse(null);
            pagament.setFactura(factura);
            factura.setEstat(EstatFactura.Pagada);
            facturaServei.guardar(factura);
            pagamentServei.guardar(pagament);

            compteBancariServei.afegirSaldo(pagament.getTotal());

            response.put("status", "success");
            response.put("chargeId", charge.getId());
            response.put("redirectUrl", "/historialPagaments");
        } catch (StripeException e) {
            response.put("status", "failed");
            response.put("error", e.getMessage());
        } catch (Exception e) {
            response.put("status", "failed");
            response.put("error", "An error occurred while processing the payment.");
        }
        return response;
    }
}
