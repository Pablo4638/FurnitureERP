package group2.projecte2.model;

import java.math.BigDecimal;
import java.util.Date;

import group2.projecte2.model.Enums.EstatFactura;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class Factura {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_factura;

  @ManyToOne
  @JoinColumn(name = "id_comanda")
  private Comanda comanda;

  private Date data_factura;
  private BigDecimal total;
  @Enumerated(EnumType.STRING)
  private EstatFactura estat;

  public Factura(Comanda comanda, Date data_factura, BigDecimal total, EstatFactura estat) {
    this.comanda = comanda;
    this.data_factura = data_factura;
    this.total = total;
    this.estat = estat;
  }
}
