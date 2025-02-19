package group2.projecte2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Proveidor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_proveidor;

  private String nom;
  private String email;
  private String telefon;
  private String direccio;
}
