package group2.projecte2.serveis.implementacio;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group2.projecte2.model.Client;
import group2.projecte2.repositori.jpa.ClientRepositori;
import group2.projecte2.serveis.ClientServei;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientServeiImpl implements ClientServei {
    @Autowired
    private ClientRepositori clientRepositori;

    @Override
    public List<Client> obtenirTots() {
        return clientRepositori.findAll();
    }

    @Override
    public void guardar(Client client) {
        clientRepositori.save(client);
    }

    @Override
    public Optional<Client> obtenirPerId(Long id) {
        return clientRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        clientRepositori.deleteById(id);
    }

    @Override
    public Long recompte() {
        return clientRepositori.count();
    }

    @Override
    public Client buscarClientQueMesGasta() {
        List<Client> clients = clientRepositori.findClientsWithMaxSpent();
        if (clients.isEmpty()) {
            throw new NoSuchElementException("No hay clientes con gastos registrados.");
        }
        return clients.get(0);
    }

    @Override
    public BigDecimal totalGastatPerClient(Long clientId) {
        return clientRepositori.findTotalSpentByClient(clientId);
    }

    @Override
    public List<Client> buscarClientsPerNom(String nombre) {
        List<Client> clients = clientRepositori.findByNomContainingIgnoreCase(nombre);
        return clients;
    }

    @Override
    public List<Client> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Client>> comparadores = Map.of(
                "nom", Comparator.comparing(Client::getNom),
                "email", Comparator.comparing(Client::getEmail),
                "telefon", Comparator.comparing(Client::getTelefon),
                "direccio", Comparator.comparing(Client::getDireccio),
                "tipus", Comparator.comparing(Client::getTipus_client));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Client> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(Client::getNom));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra y ordena la lista
        return clientRepositori.findAll().stream()
                .filter(c -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No aplica filtro si no hay valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return c.getNom().toLowerCase().contains(valor.toLowerCase());
                        case "email":
                            return c.getEmail().toLowerCase().contains(valor.toLowerCase());
                        case "telefon":
                            return c.getTelefon().toLowerCase().contains(valor.toLowerCase());
                        case "direccio":
                            return c.getDireccio().toLowerCase().contains(valor.toLowerCase());
                        case "tipus":
                            return c.getTipus_client().name().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
