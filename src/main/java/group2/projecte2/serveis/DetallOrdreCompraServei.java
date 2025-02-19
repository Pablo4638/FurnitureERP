package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.DetallOrdreCompra;
import group2.projecte2.model.OrdreCompra;

public interface DetallOrdreCompraServei {
    
    public List<DetallOrdreCompra> obtenirTots();

    public void guardar(DetallOrdreCompra detallOrdreCompra);

    public Optional<DetallOrdreCompra> obtenirPerId(Long id);

    public void eliminar(Long id);

    public List<DetallOrdreCompra> obtenirPerOrdreCompra(OrdreCompra ordreCompra);
    
    public List<DetallOrdreCompra> obtenirPerOrdreCompraId(Long idComanda);
}