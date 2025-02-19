package group2.projecte2.serveis.implementacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.FotoPerfil;
import group2.projecte2.repositori.mongo.FotoPerfilMongoRepositori;
import group2.projecte2.serveis.FotoPerfilServei;

@Service
public class FotoPerfilServeiImpl implements FotoPerfilServei {

    @Autowired
    private FotoPerfilMongoRepositori fotoPerfilMongoRepositori;

    @Override
    public FotoPerfil BuscarPerUsuari(String usuari) {
        return fotoPerfilMongoRepositori.findByUserId(usuari);
    }

    @Override
    public FotoPerfil guardar(FotoPerfil fotoPerfil) {
        FotoPerfil fotoExistente = fotoPerfilMongoRepositori.findByUserId(fotoPerfil.getUserId());

        if (fotoExistente != null) {
            fotoExistente.setFoto(fotoPerfil.getFoto());
            return fotoPerfilMongoRepositori.save(fotoExistente);
        }

        return fotoPerfilMongoRepositori.save(fotoPerfil);
    }

    @Override
    public FotoPerfil obtenirPerUsuari(String usuari) {
        return fotoPerfilMongoRepositori.findByUserId(usuari);
    }

    @Override
    public FotoPerfil actualizarFoto(String usuari, byte[] nuevaFoto) {
        FotoPerfil fotoPerfil = fotoPerfilMongoRepositori.findByUserId(usuari);

        if (fotoPerfil == null) {
            fotoPerfil = new FotoPerfil(usuari, nuevaFoto);
        } else {
            fotoPerfil.setFoto(nuevaFoto);
        }

        return fotoPerfilMongoRepositori.save(fotoPerfil);
    }

    @Override
    public void eliminarFotoPorUsuario(String usuari) {
        fotoPerfilMongoRepositori.deleteByUserId(usuari);
    }
}