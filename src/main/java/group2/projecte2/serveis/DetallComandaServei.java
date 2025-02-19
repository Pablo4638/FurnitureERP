package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.DetallComanda;

public interface DetallComandaServei {
     public List<DetallComanda> obtenirTots();

     public Optional<DetallComanda> obtenirPerId(Long id);

     public void guardar(DetallComanda detallComanda);

     public List<DetallComanda> obtenirPerComanda(Long id_comanda);
}