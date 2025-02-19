package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.OrdreCompra;

public interface OrdreCompraServei {
    public List<OrdreCompra> obtenirTots();

    public OrdreCompra guardar(OrdreCompra ordreCompra);

    public Optional<OrdreCompra> obtenirPerId(Long id);

    public void eliminar(Long id);

    public List<OrdreCompra> ordenarPerData(List<OrdreCompra> ordres);

    public List<OrdreCompra> BuscarOrdresCompletades();
    
    public List<OrdreCompra> BuscarOrdresPendents();

    List<OrdreCompra> findOrdresCompletadaCancelada();

    public List<Double> ordresVentasAnuales();

    List<OrdreCompra> filtrarYOrdenar(String filtro, String valor, String orden);

    List<OrdreCompra> filtrarYOrdenarHistorial(String filtro, String valor, String orden);
}
