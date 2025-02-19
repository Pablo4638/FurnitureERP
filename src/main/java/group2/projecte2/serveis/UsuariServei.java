package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import group2.projecte2.model.User;

public interface UsuariServei {
    public List<User> obtenirTots();

    public void guardar(User user);

    public Optional<User> obtenirPerId(Long id);

    public void eliminar(Long id);

    public String crearContrasenya();

    public void enviarMissatge(String usuari, String correu, String contrasenya);

    public void encriptarContrasenya(User user);

    public List<User> buscarAdmins();

    public List<User> buscarUsers();

    public List<User> buscarRRHH();

    public List<User> buscarInventari();

    public List<User> buscarFinances();

    public Long extractUserId(String userDetailsString);

    public Boolean comprobarPassword(String password1, String password2);

    void guardarCrear(User user);

    public User buscarPerUsuari(String username);
}