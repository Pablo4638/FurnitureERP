package group2.projecte2.serveis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import group2.projecte2.model.Client;
import group2.projecte2.model.Comanda;

public interface ComandaServei {
    public List<Comanda> obtenirTots();

    public void guardar(Comanda comanda);

    public Optional<Comanda> obtenirPerId(Long id);

    public void eliminar(Long id);

    public double calcularPorcentajeGastoCliente(Long clientId);

    public BigDecimal totalComandasAvui();

    public BigDecimal totalComandasAhir();

    public String PercentatgeDeCanvi();

    public List<Comanda> buscarComandesPendents();

    public List<Comanda> buscarComandesCompletadesCancelades();

    List<Comanda> obtenirComandesClient(Client client);

    List<Comanda> filtrarYOrdenar(String filtro, String valor, String orden);

    List<Comanda> filtrarYOrdenarHistorial(String filtro, String valor, String orden);

}
