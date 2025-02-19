package group2.projecte2.serveis.implementacio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.DetallOrdreCompra;
import group2.projecte2.model.OrdreCompra;
import group2.projecte2.repositori.jpa.DetallOrdreCompraRepositori;
import group2.projecte2.serveis.DetallOrdreCompraServei;

@Service
public class DetallOrdreCompraServeiImpl implements DetallOrdreCompraServei {
    @Autowired
    DetallOrdreCompraRepositori detallOrdreCompraRepositori;

    @Override
    public List<DetallOrdreCompra> obtenirTots() {
        return detallOrdreCompraRepositori.findAll();
    }

    @Override
    public void guardar(DetallOrdreCompra detallOrdreCompra) {
        detallOrdreCompraRepositori.save(detallOrdreCompra);
    }

    @Override
    public Optional<DetallOrdreCompra> obtenirPerId(Long id) {
        return detallOrdreCompraRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        detallOrdreCompraRepositori.deleteById(id);
    }

    public List<DetallOrdreCompra> obtenirPerOrdreCompra(OrdreCompra ordreCompra) {
        return detallOrdreCompraRepositori.findByOrdreCompra(ordreCompra);
    }

    @Override
    public List<DetallOrdreCompra> obtenirPerOrdreCompraId(Long idComanda) {
        return detallOrdreCompraRepositori.findByOrdreCompra_Id(idComanda);
    }
}
