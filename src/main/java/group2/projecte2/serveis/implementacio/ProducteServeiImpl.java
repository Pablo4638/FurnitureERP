package group2.projecte2.serveis.implementacio;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;
import group2.projecte2.repositori.jpa.ProducteRepositori;
import group2.projecte2.serveis.ProducteServei;

@Service
public class ProducteServeiImpl implements ProducteServei {
    @Autowired
    private ProducteRepositori producteRepositori;

    @Override
    public List<Producte> obtenirTots() {
        return producteRepositori.findAll();
    }

    @Override
    public void guardar(Producte producte) {
        producteRepositori.save(producte);
    }

    @Override
    public Optional<Producte> obtenirPerId(Long id) {
        return producteRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        if (!producteRepositori.existsById(id)) {
            throw new ProducteNotFoundException("El producte amb ID " + id + " no existeix.");
        }
        producteRepositori.deleteById(id);
    }

    @Override
    public List<Producte> obtenirProductesPerProveeidor(Proveidor proveidor) {
        return producteRepositori.findByProveidor(proveidor);
    }

    public class ProducteNotFoundException extends RuntimeException {
        public ProducteNotFoundException(String message) {
            super(message);
        }
    }

    @Override
    public List<Producte> buscarProductesPorNom(String nombre) {
        List<Producte> productes = producteRepositori.findByNomProducteContaining(nombre);

        return productes;
    }



    public List<Producte> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Producte>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                Producte::getNom_producte,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("descripcio", Comparator.comparing(
                Producte::getDescripcio,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("categoria", Comparator.comparing(
                producte -> producte.getCategoriaProducte() != null ? producte.getCategoriaProducte().getNom_categoria()
                        : null,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("proveidor", Comparator.comparing(
                producte -> producte.getProveidor() != null ? producte.getProveidor().getNom() : null,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("preu", Comparator.comparing(
                Producte::getPreu,
                Comparator.nullsFirst(BigDecimal::compareTo)));

        comparadores.put("stockactual", Comparator.comparing(
                Producte::getStcok_actual,
                Comparator.nullsFirst(BigDecimal::compareTo)));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Producte> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(Producte::getNom_producte));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return producteRepositori.findAll().stream()
                .filter(p -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return p.getNom_producte().toLowerCase().contains(valor.toLowerCase());
                        case "descripcio":
                            return p.getDescripcio().toString().toLowerCase().contains(valor.toLowerCase());
                        case "categoria":
                            return p.getCategoriaProducte().toString().toLowerCase().contains(valor.toLowerCase());
                        case "proveidor":
                            return p.getProveidor().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "preu":
                            return p.getPreu().toString().toLowerCase().contains(valor.toLowerCase());
                        case "stockactual":
                            return p.getStcok_actual().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}