package group2.projecte2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CategoriaProducte {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_categoria;

  private String nom_categoria;
  private String descripcio;
}