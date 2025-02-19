package group2.projecte2.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Pagament {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_pagament;

  @ManyToOne
  @JoinColumn(name = "id_factura")
  private Factura factura;

  private BigDecimal total;
  private Date data_pagament;

}
