package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import group2.projecte2.model.Asistencia;
import group2.projecte2.model.Empleat;

public interface AsistenciaServei {
    public List<Asistencia> obtenirTotes();

    public void guardar(Asistencia asistencia);

    public Optional<Asistencia> obtenirPerId(Long id);

    public void eliminar(Long id);

    public Page<Asistencia> buscarTots(Pageable pageable);

    public List<Asistencia> buscarPerEmpleat(Long empleatId);

    public void registrarInici(Empleat empleat);

    public void registrarFi(Empleat empleat);
}
