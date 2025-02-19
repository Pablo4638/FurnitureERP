package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;

public interface ProducteServei {
    public List<Producte> obtenirTots();

    public void guardar(Producte producte);

    public Optional<Producte> obtenirPerId(Long id);

    public void eliminar(Long id);

    public List<Producte> obtenirProductesPerProveeidor(Proveidor proveidor);

    public List<Producte> buscarProductesPorNom(String nombre);

    List<Producte> filtrarYOrdenar(String filtro, String valor, String orden);
    
}
