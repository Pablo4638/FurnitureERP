package group2.projecte2.model;

import java.util.List;

import group2.projecte2.model.Enums.ClientTipusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Client {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_client;

  private String nom;
  private String email;
  private String telefon;
  private String direccio;

  @OneToMany(mappedBy = "client")
  private List<Comanda> comandes;

  @Enumerated(EnumType.STRING)
  private ClientTipusEnum tipus_client;
}
