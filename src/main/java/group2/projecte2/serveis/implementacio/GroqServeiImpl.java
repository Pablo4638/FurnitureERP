package group2.projecte2.serveis.implementacio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import group2.projecte2.model.Client;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqServeiImpl {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    public GroqServeiImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> obtenerRespuestaIA(String mensajeUsuario, List<Producte> productos, List<Client> clients, List<Proveidor> proveidors) {
    // Crear contexto adicional basado en productos
    StringBuilder contexto = new StringBuilder("Datos relevantes:\n");
    if (!productos.isEmpty()) {
        contexto.append("Productos:\n");
        for (Producte producto : productos) {
            contexto.append("- ")
                    .append(producto.getNom_producte())
                    .append(": ")
                    .append(producto.getPreu())
                    .append(" €, ")
                    .append(producto.getStcok_actual())
                    .append(" unidades disponibles.\n");
        }
    }

    // Añadir información de clientes
    if (!clients.isEmpty()) {
        contexto.append("Clientes:\n");
        for (Client cliente : clients) {
            contexto.append("Nom: ")
                    .append(cliente.getNom())
                    .append(" Telefon: ")
                    .append(cliente.getTelefon())
                    .append(", Email: ")
                    .append(cliente.getEmail())
                    .append(", Direccio: ")
                    .append(cliente.getDireccio())
                    .append("\n");
        }
    }
    if (!proveidors.isEmpty()) {
        contexto.append("Proveidors:\n");
        for (Proveidor proveidor : proveidors) {
            contexto.append("Nom: ")
                    .append(proveidor.getNom())
                    .append(" Telefon: ")
                    .append(proveidor.getTelefon())
                    .append(", Email: ")
                    .append(proveidor.getEmail())
                    .append(", Direccio: ")
                    .append(proveidor.getDireccio())
                    .append("\n");
        }
    }
    

    // Construir el cuerpo del request
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "llama3-70b-8192"); // Modelo de Groq o OpenAI
    requestBody.put("messages", List.of(
            Map.of("role", "system", "content", contexto.toString()),
            Map.of("role", "user", "content", mensajeUsuario)
    ));

    // Realizar la llamada HTTP POST
    return webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer " + apiKey)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class);
}



}
