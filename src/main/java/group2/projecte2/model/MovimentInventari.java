package group2.projecte2.model;

import java.util.Date;

import group2.projecte2.model.Enums.TipusMoviment;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class MovimentInventari {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_moviment;

  @ManyToOne
  @JoinColumn(name = "id_producte")
  private Producte producte;

  @Enumerated(EnumType.STRING)
  private TipusMoviment tipus_moviment;
  private Date data_moviment;
  private Integer quanttitat;
}
