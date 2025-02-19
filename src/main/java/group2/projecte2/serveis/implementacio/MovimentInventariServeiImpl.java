package group2.projecte2.serveis.implementacio;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Enums.TipusMoviment;
import group2.projecte2.repositori.jpa.MovimentInventariRepositori;
import group2.projecte2.serveis.MovimentInventariServei;

@Service
public class MovimentInventariServeiImpl implements MovimentInventariServei {

    @Autowired
    private MovimentInventariRepositori movimentInventariRepositori;

    @Override
    public List<MovimentInventari> obtenirTots() {
        return movimentInventariRepositori.findAll();
    }

    @Override
    public void guardar(MovimentInventari movimentInventari) {
        movimentInventariRepositori.save(movimentInventari);
    }

    @Override
    public Optional<MovimentInventari> obtenirPerId(Long id) {
        return movimentInventariRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        movimentInventariRepositori.deleteById(id);
    }

    @Override
    public List<MovimentInventari> ObtenirMovimentsEntrada() {
        return movimentInventariRepositori.findByTipusMoviment(TipusMoviment.Entrada);
    }

    @Override
    public List<MovimentInventari> ObtenirMovimentsSortida() {
        return movimentInventariRepositori.findByTipusMoviment(TipusMoviment.Sortida);
    }

    @Override
    public void crearMoviment(Producte producte, TipusMoviment tipusMoviment, Integer quantitat) {
        MovimentInventari moviment = new MovimentInventari();
        moviment.setProducte(producte);
        moviment.setTipus_moviment(tipusMoviment);
        moviment.setData_moviment(new Date());
        moviment.setQuanttitat(quantitat);

        movimentInventariRepositori.save(moviment);
    }

    public List<MovimentInventari> filtrarYOrdenarEntrades(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<MovimentInventari>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                movimentInventari -> movimentInventari.getProducte() != null
                        ? movimentInventari.getProducte().getNom_producte()
                        : null,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("datamoviment", Comparator.comparing(
                MovimentInventari::getData_moviment,
                Comparator.nullsFirst(Date::compareTo)));

        comparadores.put("quantitat", Comparator.comparing(
                MovimentInventari::getQuanttitat,
                Comparator.nullsFirst(Integer::compareTo)));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<MovimentInventari> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(
                        movimentInventari -> movimentInventari.getProducte() != null
                                ? movimentInventari.getProducte().getNom_producte()
                                : null,
                        Comparator.nullsFirst(String::compareTo)));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return movimentInventariRepositori.findAll().stream()
                .filter(m -> m.getTipus_moviment() == TipusMoviment.Entrada)
                .filter(m -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return m.getProducte().toString().toLowerCase().contains(valor.toLowerCase());
                        case "datamoviment":
                            return m.getData_moviment().toString().toLowerCase().contains(valor.toLowerCase());
                        case "quantitat":
                            return m.getQuanttitat().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }

    public List<MovimentInventari> filtrarYOrdenarSortidas(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<MovimentInventari>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                movimentInventari -> movimentInventari.getProducte() != null
                        ? movimentInventari.getProducte().getNom_producte()
                        : null,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("datamoviment", Comparator.comparing(
                MovimentInventari::getData_moviment,
                Comparator.nullsFirst(Date::compareTo)));

        comparadores.put("quantitat", Comparator.comparing(
                MovimentInventari::getQuanttitat,
                Comparator.nullsFirst(Integer::compareTo)));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<MovimentInventari> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(
                        movimentInventari -> movimentInventari.getProducte() != null
                                ? movimentInventari.getProducte().getNom_producte()
                                : null,
                        Comparator.nullsFirst(String::compareTo)));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return movimentInventariRepositori.findAll().stream()
                .filter(m -> m.getTipus_moviment() == TipusMoviment.Sortida)
                .filter(m -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return m.getProducte().toString().toLowerCase().contains(valor.toLowerCase());
                        case "datamoviment":
                            return m.getData_moviment().toString().toLowerCase().contains(valor.toLowerCase());
                        case "quantitat":
                            return m.getQuanttitat().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }

    public List<MovimentInventari> filtrarYOrdenarHistorial(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<MovimentInventari>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                movimentInventari -> movimentInventari.getProducte() != null
                        ? movimentInventari.getProducte().getNom_producte()
                        : null,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("datamoviment", Comparator.comparing(
                MovimentInventari::getData_moviment,
                Comparator.nullsFirst(Date::compareTo)));

        comparadores.put("quantitat", Comparator.comparing(
                MovimentInventari::getQuanttitat,
                Comparator.nullsFirst(Integer::compareTo)));

        comparadores.put("tipusmoviment", Comparator.comparing(
                MovimentInventari::getTipus_moviment,
                Comparator.nullsFirst(Comparator.naturalOrder())));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<MovimentInventari> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(
                        movimentInventari -> movimentInventari.getProducte() != null
                                ? movimentInventari.getProducte().getNom_producte()
                                : null,
                        Comparator.nullsFirst(String::compareTo)));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return movimentInventariRepositori.findAll().stream()
                .filter(m -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return m.getProducte().toString().toLowerCase().contains(valor.toLowerCase());
                        case "datamoviment":
                            return m.getData_moviment().toString().toLowerCase().contains(valor.toLowerCase());
                        case "quantitat":
                            return m.getQuanttitat().toString().toLowerCase().contains(valor.toLowerCase());
                        case "tipusmoviment":
                            return m.getTipus_moviment().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
