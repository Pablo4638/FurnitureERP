package group2.projecte2.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CompteBancari {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_compte_bancari;

  private String iban;
  private BigDecimal saldo;

  public CompteBancari() {
    this.saldo = BigDecimal.ZERO;
  }

  public void afegirSaldo(BigDecimal saldo) {
    this.saldo = this.saldo.add(saldo);
  }
}
