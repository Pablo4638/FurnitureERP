package group2.projecte2.repositori.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group2.projecte2.model.Client;
import group2.projecte2.model.Producte;

@Repository
public interface ClientRepositori extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c " +
            "JOIN c.comandes com " +
            "GROUP BY c.id_client " +
            "ORDER BY SUM(com.total) DESC")
    List<Client> findClientsWithMaxSpent();

    @Query("SELECT SUM(c.total) FROM Comanda c WHERE c.client.id_client = :clientId")
    BigDecimal findTotalSpentByClient(Long clientId);
    List<Client> findByNomContainingIgnoreCase(@Param("nombre") String nombre);
}
