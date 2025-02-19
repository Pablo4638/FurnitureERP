package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.Producte;
import group2.projecte2.model.Enums.TipusMoviment;

public interface MovimentInventariServei {
    public List<MovimentInventari> obtenirTots();

    public void guardar(MovimentInventari movimentInventari);

    public Optional<MovimentInventari> obtenirPerId(Long id);

    public void eliminar(Long id);

    public List<MovimentInventari> ObtenirMovimentsEntrada();

    public List<MovimentInventari> ObtenirMovimentsSortida();

    public void crearMoviment(Producte producte, TipusMoviment tipusMoviment, Integer quantitat);

    List<MovimentInventari> filtrarYOrdenarEntrades(String filtro, String valor, String orden);

    List<MovimentInventari> filtrarYOrdenarSortidas(String filtro, String valor, String orden);

    List<MovimentInventari> filtrarYOrdenarHistorial(String filtro, String valor, String orden);
}
