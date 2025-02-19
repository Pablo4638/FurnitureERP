package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.Proveidor;

public interface ProveidorServei {
   public List<Proveidor> obtenirTots();

   public void guardar(Proveidor proveidor);

   public Optional<Proveidor> obtenirPerId(Long id);

   public void eliminar(Long id);

   List<Proveidor> filtrarYOrdenar(String filtro, String valor, String orden);
   public List<Proveidor> buscarProveidorsPerNom(String nombre);

}
