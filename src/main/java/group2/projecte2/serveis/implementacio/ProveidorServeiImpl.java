package group2.projecte2.serveis.implementacio;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Client;
import group2.projecte2.model.Proveidor;
import group2.projecte2.repositori.jpa.ProveidorRepositori;
import group2.projecte2.serveis.ProveidorServei;

@Service
public class ProveidorServeiImpl implements ProveidorServei {
    @Autowired
    private ProveidorRepositori proveidorRepositori;

    @Override
    public List<Proveidor> obtenirTots() {
        return proveidorRepositori.findAll();
    }

    @Override
    public void guardar(Proveidor proveidor) {
        proveidorRepositori.save(proveidor);
    }

    @Override
    public Optional<Proveidor> obtenirPerId(Long id) {
        return proveidorRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        proveidorRepositori.deleteById(id);
    }

    public List<Proveidor> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Proveidor>> comparadores = Map.of(
                "nom", Comparator.comparing(Proveidor::getNom),
                "email", Comparator.comparing(Proveidor::getEmail),
                "telefon", Comparator.comparing(Proveidor::getTelefon),
                "direccio", Comparator.comparing(Proveidor::getDireccio));
    
        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Proveidor> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(Proveidor::getNom) 
        );
    
        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }
    
        // Filtra y ordena la lista
        return proveidorRepositori.findAll().stream()
                .filter(p -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No aplica filtro si no hay valor
                    }
    
                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return p.getNom().toLowerCase().contains(valor.toLowerCase());
                        case "email":
                            return p.getEmail().toLowerCase().contains(valor.toLowerCase());
                        case "telefon":
                            return p.getTelefon().toLowerCase().contains(valor.toLowerCase());
                        case "direccio":
                            return p.getDireccio().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }    
    @Override
    public List<Proveidor> buscarProveidorsPerNom(String nombre) {
        List<Proveidor> proveidors = proveidorRepositori.findByNomContainingIgnoreCase(nombre);
        return proveidors;
    }
}