package group2.projecte2.serveis.implementacio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.DetallComanda;
import group2.projecte2.repositori.jpa.DetallComandaRepositori;
import group2.projecte2.serveis.DetallComandaServei;

@Service
public class DetallComandaServeiImpl implements DetallComandaServei {
    @Autowired
    DetallComandaRepositori detallComandaRepositori;

    public List<DetallComanda> obtenirTots() {
        return detallComandaRepositori.findAll();
    }

    @Override
    public Optional<DetallComanda> obtenirPerId(Long id) {
        return detallComandaRepositori.findById(id);
    }

    @Override
    public void guardar(DetallComanda detallComanda) {
        detallComandaRepositori.save(detallComanda);
    }

    @Override
    public List<DetallComanda> obtenirPerComanda(Long id_comanda) {
        return detallComandaRepositori.findByComanda_Id(id_comanda);
    }
}