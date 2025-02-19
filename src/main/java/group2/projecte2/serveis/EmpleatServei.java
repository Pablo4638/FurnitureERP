package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.Empleat;

public interface EmpleatServei {
    public List<Empleat> obtenirTots();

    public void guardar(Empleat empleat);

    public Optional<Empleat> obtenirPerId(Long id);

    public void eliminar(Long id);

    public Long recompte();

    public Empleat obtenerEmpleadoPorCorreo(String email);

    public String enviarCodigoVerificacion(String correu);

    List<Empleat> filtrarYOrdenar(String filtro, String valor, String orden);
}
