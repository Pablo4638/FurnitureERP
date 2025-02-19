package group2.projecte2.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = "ordreCompra")
public class DetallOrdreCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_detall;

  @ManyToOne
  @JoinColumn(name = "id_ordre")
  private OrdreCompra ordreCompra;

  @ManyToOne
  @JoinColumn(name = "id_producte")
  private Producte producte;

  private Integer quantitat;
  private BigDecimal preu_unitari;
  private BigDecimal subtotal;
}