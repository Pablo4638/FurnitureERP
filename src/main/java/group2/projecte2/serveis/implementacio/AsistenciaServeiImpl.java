package group2.projecte2.serveis.implementacio;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Asistencia;
import group2.projecte2.model.Empleat;
import group2.projecte2.repositori.jpa.AsistenciaRepositori;
import group2.projecte2.serveis.AsistenciaServei;

@Service
public class AsistenciaServeiImpl implements AsistenciaServei {
    @Autowired
    private AsistenciaRepositori asistenciaRepositori;

    public List<Asistencia> obtenirTotes() {
        return asistenciaRepositori.findAll();
    }

    @Override
    public void guardar(Asistencia asistencia) {
        asistenciaRepositori.save(asistencia);
    }

    @Override
    public Optional<Asistencia> obtenirPerId(Long id) {
        return asistenciaRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        asistenciaRepositori.deleteById(id);
    }

    public Page<Asistencia> buscarTots(Pageable pageable) {
        return asistenciaRepositori.findAll(pageable);
    }

    public List<Asistencia> buscarPerEmpleat(Long empleatId) {
        return asistenciaRepositori.findByEmpleatIdEmpleat(empleatId);
    }

    @Override
    public void registrarInici(Empleat empleat) {
        Asistencia asistencia = new Asistencia();
        asistencia.setData(new java.util.Date());
        asistencia.setEmpleat(empleat);
        asistencia.setHora_entrada(new java.util.Date());
        asistenciaRepositori.save(asistencia);
    }

    @Override
    public void registrarFi(Empleat empleat) {
        Optional<Asistencia> optionalAsistencia = asistenciaRepositori
                .findUltimaAsistenciaPorEmpleado(empleat.getId_empleat());

        if (optionalAsistencia.isEmpty()) {
            throw new NoSuchElementException("No hi ha cap assistència activa per a aquest empleat.");
        }

        Asistencia asistencia = optionalAsistencia.get();

        if (asistencia.getHora_sortida() != null) {
            throw new IllegalStateException("L'assistència ja està finalitzada.");
        }

        asistencia.setHora_sortida(new java.util.Date());
        asistenciaRepositori.save(asistencia);
    }
}