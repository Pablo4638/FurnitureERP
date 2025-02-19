package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.Pagament;

public interface PagamentServei {

  List<Pagament> obtenirTots();

  Pagament guardar(Pagament pagament);

  Optional<Pagament> obtenirPerId(Long id);

  void eliminar(Long id);

  List<Pagament> filtrarYOrdenar(String filtro, String valor, String orden);
}
