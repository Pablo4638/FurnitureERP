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

@Data
@Entity
public class Comanda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_comanda;

  @ManyToOne
  @JoinColumn(name = "id_client")
  private Client client;

  private Date data_comanda;
  @Enumerated(EnumType.STRING)
  private EstatCompraVenta estat;
  private BigDecimal total;

  @OneToMany(mappedBy = "comanda")
  private List<DetallComanda> detallComandes;
}