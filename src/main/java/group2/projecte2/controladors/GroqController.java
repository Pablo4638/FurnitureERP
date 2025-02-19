package group2.projecte2.controladors;

import group2.projecte2.model.Client;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;
import group2.projecte2.serveis.implementacio.ClientServeiImpl;
import group2.projecte2.serveis.implementacio.GroqServeiImpl;
import group2.projecte2.serveis.implementacio.ProveidorServeiImpl;
import group2.projecte2.serveis.ProducteServei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/groq")
public class GroqController {

    private static final Logger logger = LoggerFactory.getLogger(GroqController.class);

    @Autowired
    private GroqServeiImpl groqServei;

    @Autowired
    private ProducteServei producteServei;

    @Autowired
    private ClientServeiImpl clientServei;

    @Autowired
    private ProveidorServeiImpl proveidorServei;

    /**
     * Endpoint per gestionar les preguntes a la IA i buscar productes per nom.
     *
     * @param mensaje El missatge enviat per l'usuari.
     * @return Una resposta amb una llista de productes trobats o una resposta de la IA.
     */
    @PostMapping("/preguntar")
public Mono<ResponseEntity<String>> preguntarIA(@RequestParam String mensaje) {
    List<Producte> productosTrobats = producteServei.buscarProductesPorNom(mensaje);
    List<Client> clientsTrobats = clientServei.buscarClientsPerNom(mensaje);
    List<Proveidor> proveidorsTrobats = proveidorServei.buscarProveidorsPerNom(mensaje);

    return groqServei.obtenerRespuestaIA(mensaje, productosTrobats, clientsTrobats, proveidorsTrobats)
        .onErrorResume(error -> {
            logger.error("Error al obtener respuesta de la API", error);
            return Mono.just("Lo siento, ocurrió un error al procesar tu solicitud.");
        })
        .map(respuesta -> ResponseEntity.ok(respuesta));

}
}
