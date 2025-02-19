package group2.projecte2.model;

import java.math.BigDecimal;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class DetallComanda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_detall;

  //Hibernate se encargará de guardar la entidad Comanda automáticamente al guardar DetallComanda.
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_comanda")
  private Comanda comanda;

  @ManyToOne
  @JoinColumn(name = "id_producte")
  private Producte producte;

  private Integer quantitat;
  private BigDecimal subtotal;
}