package group2.projecte2.serveis.implementacio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Nomina;
import group2.projecte2.repositori.jpa.NominesRepositori;
import group2.projecte2.serveis.NominaServei;

@Service
public class NominaServeiImpl implements NominaServei {
    @Autowired
    protected NominesRepositori nominesRepositori;

    @Override
    public List<Nomina> obtenirTots() {
        return nominesRepositori.findAll();
    }

}
