package group2.projecte2.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import group2.projecte2.model.Enums.EstatCompraVenta;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = "detallOrdreCompra")
public class OrdreCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_ordre;

  @ManyToOne
  @JoinColumn(name = "id_proveidor")
  private Proveidor proveidor;

  private Date data_ordre;
  @Enumerated(EnumType.STRING) // Almacena el valor del Enum como texto en la base de datos
  private EstatCompraVenta estat;
  private BigDecimal total;

  @OneToMany(mappedBy = "ordreCompra")
  private List<DetallOrdreCompra> detallOrdreCompra;
}
