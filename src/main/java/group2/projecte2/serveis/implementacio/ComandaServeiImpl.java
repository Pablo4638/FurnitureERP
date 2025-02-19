package group2.projecte2.serveis.implementacio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Client;
import group2.projecte2.model.Comanda;
import group2.projecte2.model.Enums.EstatCompraVenta;
import group2.projecte2.repositori.jpa.ComandaRepositori;
import group2.projecte2.serveis.ComandaServei;

@Service
public class ComandaServeiImpl implements ComandaServei {

    @Autowired
    ComandaRepositori comandaRepositori;

    public List<Comanda> obtenirTots() {
        return comandaRepositori.findAll();
    }

    @Override
    public void guardar(Comanda comanda) {
        comandaRepositori.save(comanda);
    }

    @Override
    public Optional<Comanda> obtenirPerId(Long id) {
        return comandaRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        comandaRepositori.deleteById(id);
    }

    @Override
    public double calcularPorcentajeGastoCliente(Long clientId) {

        List<Comanda> todasComandas = comandaRepositori.findAll();

        BigDecimal totalComandas = todasComandas.stream()
                .map(Comanda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalComandas.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal totalCliente = todasComandas.stream()
                .filter(comanda -> comanda.getClient().getId_client().equals(clientId))
                .map(Comanda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalCliente.divide(totalComandas, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Override
    public BigDecimal totalComandasAvui() {
        BigDecimal totalToday = comandaRepositori.sumComandasTotalToday();
        return totalToday != null ? totalToday : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal totalComandasAhir() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Date sqlYesterday = Date.valueOf(yesterday);

        BigDecimal totalYesterday = comandaRepositori.sumComandasTotalYesterday(sqlYesterday);
        return totalYesterday != null ? totalYesterday : BigDecimal.ZERO;
    }

    @Override
    public String PercentatgeDeCanvi() {
        BigDecimal totalToday = totalComandasAvui();
        BigDecimal totalYesterday = totalComandasAhir();

        if (totalYesterday == null || totalYesterday.equals(BigDecimal.ZERO)) {
            return "0";
        }

        BigDecimal difference = totalToday.subtract(totalYesterday);
        BigDecimal percentageChange = difference.divide(totalYesterday, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        BigDecimal roundedPercentage = percentageChange.stripTrailingZeros();
        return roundedPercentage.toPlainString();
    }

    public List<Comanda> buscarComandesPendents() {
        return comandaRepositori.findComandesPendents();
    }

    @Override
    public List<Comanda> buscarComandesCompletadesCancelades() {
        return comandaRepositori.findComandesCompletadaCancelada();
    }

    @Override
    public List<Comanda> obtenirComandesClient(Client client) {
        return comandaRepositori.findByClient(client);
    }

    public List<Comanda> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Comanda>> comparadores = Map.of(
                "client", Comparator.comparing(comanda -> comanda.getClient().getNom()),
                "datacomanda", Comparator.comparing(Comanda::getData_comanda),
                "total", Comparator.comparing(Comanda::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Comanda> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(comanda -> comanda.getClient().getNom()));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra y ordena la lista
        return comandaRepositori.findAll().stream()
                .filter(c -> c.getEstat() == EstatCompraVenta.Pendent)
                .filter(c -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No aplica filtro si no hay valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "client":
                            return c.getClient().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "datacomanda":
                            return c.getData_comanda().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return c.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }

    public List<Comanda> filtrarYOrdenarHistorial(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Comanda>> comparadores = Map.of(
                "client", Comparator.comparing(comanda -> comanda.getClient().getNom()),
                "datacomanda", Comparator.comparing(Comanda::getData_comanda),
                "estat", Comparator.comparing(Comanda::getEstat),
                "total", Comparator.comparing(Comanda::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Comanda> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(comanda -> comanda.getClient().getNom()));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        return comandaRepositori.findAll().stream()
                .filter(o -> o.getEstat() == EstatCompraVenta.Completada || o.getEstat() == EstatCompraVenta.Cancelada)
                .filter(c -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; 
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "client":
                            return c.getClient().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "datacomanda":
                            return c.getData_comanda().toString().toLowerCase().contains(valor.toLowerCase());
                        case "estat":
                            return c.getEstat().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return c.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
