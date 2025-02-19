package group2.projecte2.serveis;

import group2.projecte2.model.FotoPerfil;

public interface FotoPerfilServei {
    public FotoPerfil guardar(FotoPerfil fotoPerfil);

    public FotoPerfil BuscarPerUsuari(String usuari);

    public FotoPerfil obtenirPerUsuari(String usuario);

    public FotoPerfil actualizarFoto(String usuario, byte[] nuevaFoto);

    public void eliminarFotoPorUsuario(String usuari);
}
