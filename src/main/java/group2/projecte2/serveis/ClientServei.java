package group2.projecte2.serveis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import group2.projecte2.model.Client;
import group2.projecte2.model.Producte;

public interface ClientServei {
    public List<Client> obtenirTots();

    public void guardar(Client client);

    public Optional<Client> obtenirPerId(Long id);

    public void eliminar(Long id);

    public Long recompte();

    public Client buscarClientQueMesGasta();

    public BigDecimal totalGastatPerClient(Long clientId);

    public List<Client> buscarClientsPerNom(String nombre);
    List<Client> filtrarYOrdenar(String filtro, String valor, String orden);
    
}
