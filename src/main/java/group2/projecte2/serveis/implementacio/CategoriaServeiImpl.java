package group2.projecte2.serveis.implementacio;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.CategoriaProducte;
import group2.projecte2.repositori.jpa.CategoriaRepositori;
import group2.projecte2.serveis.CategoriaServei;

@Service
public class CategoriaServeiImpl implements CategoriaServei {
    @Autowired
    CategoriaRepositori categoriaRepositori;

    public List<CategoriaProducte> obtenirTotes() {
        return categoriaRepositori.findAll();
    }

    @Override
    public void guardar(CategoriaProducte comanda) {
        categoriaRepositori.save(comanda);
    }

    @Override
    public Optional<CategoriaProducte> obtenirPerId(Long id) {
        return categoriaRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        categoriaRepositori.deleteById(id);
    }

    public List<CategoriaProducte> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<CategoriaProducte>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                CategoriaProducte::getNom_categoria,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("descripcio", Comparator.comparing(
                CategoriaProducte::getDescripcio,
                Comparator.nullsFirst(String::compareTo)));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<CategoriaProducte> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(CategoriaProducte::getNom_categoria));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return categoriaRepositori.findAll().stream()
                .filter(c -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return c.getNom_categoria().toLowerCase().contains(valor.toLowerCase());
                        case "descripcio":
                            return c.getDescripcio().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
