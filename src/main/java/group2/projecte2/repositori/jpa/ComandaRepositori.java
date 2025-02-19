package group2.projecte2.repositori.jpa;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group2.projecte2.model.Client;
import group2.projecte2.model.Comanda;

public interface ComandaRepositori extends JpaRepository<Comanda, Long> {

    @Query("SELECT c FROM Comanda c ORDER BY c.data_comanda DESC, c.total DESC")
    List<Comanda> findUltimesComandes();

    @Query("SELECT SUM(c.total) FROM Comanda c WHERE c.data_comanda = CURRENT_DATE AND c.estat = 'COMPLETADA'")
    BigDecimal sumComandasTotalToday();

    @Query("SELECT SUM(c.total) FROM Comanda c WHERE c.data_comanda = :yesterday AND c.estat = 'COMPLETADA'")
    BigDecimal sumComandasTotalYesterday(@Param("yesterday") Date yesterday);

    @Query("SELECT c FROM Comanda c WHERE c.estat = 'Pendent'")
    List<Comanda> findComandesPendents();

    @Query("SELECT c FROM Comanda c WHERE c.estat = 'Completada' OR estat = 'Cancelada'")
    List<Comanda> findComandesCompletadaCancelada();

    List<Comanda> findByClient(Client client);
}
