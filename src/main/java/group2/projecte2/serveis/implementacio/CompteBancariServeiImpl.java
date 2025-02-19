package group2.projecte2.serveis.implementacio;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.CompteBancari;
import group2.projecte2.repositori.jpa.CompteBancariRepositori;
import group2.projecte2.serveis.CompteBancariServei;

@Service
public class CompteBancariServeiImpl implements CompteBancariServei {

  @Autowired
  private CompteBancariRepositori compteBancariRepositori;

  @Override
  public void afegirSaldo(BigDecimal saldo) {
    CompteBancari compte = compteBancariRepositori.findById(1L).orElse(new CompteBancari());
    compte.afegirSaldo(saldo);
    compteBancariRepositori.save(compte);
  }

  @Override
  public BigDecimal obtenirSaldo() {
    CompteBancari compte = compteBancariRepositori.findById(1L).orElse(new CompteBancari());
    return compte.getSaldo();
  }
}
