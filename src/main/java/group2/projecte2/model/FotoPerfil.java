package group2.projecte2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "fotoPerfil") // Define el nombre de la colección en MongoDB
public class FotoPerfil {

    @Id
    private String id;

    private String userId;

    private byte[] foto;

    public FotoPerfil(String userId, byte[] foto) {
        this.userId = userId;
        this.foto = foto;
    }
}
