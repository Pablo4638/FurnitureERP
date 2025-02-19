package group2.projecte2.serveis.implementacio;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.OrdreCompra;
import group2.projecte2.model.Enums.EstatCompraVenta;
import group2.projecte2.repositori.jpa.OrdreCompraRepositori;
import group2.projecte2.serveis.OrdreCompraServei;

@Service
public class OrdreCompraServeiImpl implements OrdreCompraServei {
    @Autowired
    private OrdreCompraRepositori ordreCompraRepositori;

    @Override
    public List<OrdreCompra> obtenirTots() {
        return ordreCompraRepositori.findAll();
    }

    @Override
    public OrdreCompra guardar(OrdreCompra ordreCompra) {
        return ordreCompraRepositori.save(ordreCompra);
    }

    @Override
    public Optional<OrdreCompra> obtenirPerId(Long id) {
        return ordreCompraRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        ordreCompraRepositori.deleteById(id);
    }

    @Override
    public List<OrdreCompra> ordenarPerData(List<OrdreCompra> ordres) {
        Collections.sort(ordres, (o1, o2) -> {
            if (o1.getData_ordre() == null && o2.getData_ordre() == null) {
                return 0;
            }
            if (o1.getData_ordre() == null) {
                return 1;
            }
            if (o2.getData_ordre() == null) {
                return -1;
            }
            return o2.getData_ordre().compareTo(o1.getData_ordre());
        });

        return ordres;
    }

    @Override
    public List<OrdreCompra> BuscarOrdresCompletades() {
        return ordreCompraRepositori.findOrdresCompletades();
    }

    @Override
    public List<OrdreCompra> BuscarOrdresPendents() {
        return ordreCompraRepositori.findOrdresPendents();
    }

    @Override
    public List<OrdreCompra> findOrdresCompletadaCancelada() {
        return ordreCompraRepositori.findOrdresCompletadaCancelada();
    }

    @Override
    public List<Double> ordresVentasAnuales() {
        List<Double> ventasAnuales = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 3; i >= 0; i--) {
            int year = today.getYear() - i;
            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);

            Date startDate = Date.valueOf(startOfYear);
            Date endDate = Date.valueOf(endOfYear);

            List<OrdreCompra> factures = ordreCompraRepositori.findFacturesByYear(startDate, endDate);

            double totalVentas = factures.stream()
                    .filter(ordreCompra -> ordreCompra.getEstat() == EstatCompraVenta.Completada)
                    .mapToDouble(ordreCompra -> ordreCompra.getTotal().doubleValue())
                    .sum();

            ventasAnuales.add(totalVentas);

            System.out.println("Ventas totales (Pagadas) para el año " + year + ": " + totalVentas);
        }

        return ventasAnuales;
    }

    public List<OrdreCompra> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<OrdreCompra>> comparadores = Map.of(
                "proveidor", Comparator.comparing(ordre -> ordre.getProveidor().getNom()),
                "dataordre", Comparator.comparing(OrdreCompra::getData_ordre),
                "total", Comparator.comparing(OrdreCompra::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<OrdreCompra> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(ordre -> ordre.getProveidor().getNom()));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return ordreCompraRepositori.findAll().stream()
                .filter(o -> o.getEstat() == EstatCompraVenta.Pendent) // Filtrar solo estado "pendent"
                .filter(o -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "proveidor":
                            return o.getProveidor().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "dataordre":
                            return o.getData_ordre().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return o.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }

    public List<OrdreCompra> filtrarYOrdenarHistorial(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<OrdreCompra>> comparadores = Map.of(
                "proveidor", Comparator.comparing(ordre -> ordre.getProveidor().getNom()),
                "dataordre", Comparator.comparing(OrdreCompra::getData_ordre),
                "estat", Comparator.comparing(OrdreCompra::getEstat),
                "total", Comparator.comparing(OrdreCompra::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<OrdreCompra> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(ordre -> ordre.getProveidor().getNom()) // Valor predeterminado
        );

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        return ordreCompraRepositori.findAll().stream()
                .filter(o -> o.getEstat() == EstatCompraVenta.Completada || o.getEstat() == EstatCompraVenta.Cancelada)
                .filter(o -> {
                    // Si el filtro es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true;
                    }

                    // Aplica el filtro de acuerdo al campo seleccionado
                    switch (filtro.toLowerCase()) {
                        case "proveidor":
                            return o.getProveidor().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "dataordre":
                            return o.getData_ordre().toString().toLowerCase().contains(valor.toLowerCase());
                        case "estat":
                            return o.getEstat().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return o.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true;
                    }
                })
                .sorted(comparador)
                .collect(Collectors.toList());
    }
}
