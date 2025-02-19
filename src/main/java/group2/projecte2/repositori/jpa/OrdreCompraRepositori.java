package group2.projecte2.repositori.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import group2.projecte2.model.OrdreCompra;

public interface OrdreCompraRepositori extends JpaRepository<OrdreCompra, Long> {

    @Query("SELECT o FROM OrdreCompra o WHERE o.estat = 'Completada'")
    List<OrdreCompra> findOrdresCompletades();

    @Query("SELECT o FROM OrdreCompra o WHERE o.estat = 'Pendent'")
    List<OrdreCompra> findOrdresPendents();

    @Query("SELECT o FROM OrdreCompra o WHERE o.estat = 'Completada' OR estat = 'Cancelada'")
    List<OrdreCompra> findOrdresCompletadaCancelada();

    @Query("SELECT o FROM OrdreCompra o WHERE o.data_ordre BETWEEN :startDate AND :endDate")
    List<OrdreCompra> findFacturesByYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    

}
