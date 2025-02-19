package group2.projecte2.model;

import java.math.BigDecimal;

import group2.projecte2.model.Enums.UnitatMesura;
import jakarta.persistence.CascadeType;
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
public class Producte {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_producte;

  private String nom_producte;
  private String descripcio;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "id_categoria")
  private CategoriaProducte categoriaProducte;

  private BigDecimal preu;
  private BigDecimal stcok_actual;
  @Enumerated(EnumType.STRING)
  private UnitatMesura unitat_mesura;

  @ManyToOne
  @JoinColumn(name = "id_proveidor")
  private Proveidor proveidor;
}
