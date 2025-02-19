package group2.projecte2.repositori.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import group2.projecte2.model.Factura;

public interface FacturaRepositori extends JpaRepository<Factura, Long> {

    @Query("SELECT f FROM Factura f WHERE f.data_factura BETWEEN :startDate AND :endDate")
    List<Factura> findFacturesByYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
  @Query("SELECT c FROM Factura c WHERE c.estat = 'Pendent'")
  List<Factura> findFacturaPendent();
}
