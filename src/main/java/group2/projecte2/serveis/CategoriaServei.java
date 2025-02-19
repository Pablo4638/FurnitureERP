package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.CategoriaProducte;

public interface CategoriaServei {
    public List<CategoriaProducte> obtenirTotes();

    public void guardar(CategoriaProducte categoria);

    public Optional<CategoriaProducte> obtenirPerId(Long id);

    public void eliminar(Long id);

    List<CategoriaProducte> filtrarYOrdenar(String filtro, String valor, String orden);
}
