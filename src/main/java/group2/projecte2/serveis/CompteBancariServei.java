package group2.projecte2.serveis;

import java.math.BigDecimal;

public interface CompteBancariServei {
  void afegirSaldo(BigDecimal saldo);

  BigDecimal obtenirSaldo();
}
