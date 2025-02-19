package group2.projecte2.repositori.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import group2.projecte2.model.FotoPerfil;

public interface FotoPerfilMongoRepositori extends MongoRepository<FotoPerfil, String> {
    FotoPerfil findByUserId(String userId);

    FotoPerfil deleteByUserId(String userId);

}
