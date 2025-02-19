package group2.projecte2.repositori.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group2.projecte2.model.DetallOrdreCompra;
import group2.projecte2.model.OrdreCompra;

public interface DetallOrdreCompraRepositori extends JpaRepository <DetallOrdreCompra, Long>{
List<DetallOrdreCompra> findByOrdreCompra(OrdreCompra ordreCompra);

@Query("SELECT det FROM DetallOrdreCompra det WHERE det.ordreCompra.id = :id_ordre")
List<DetallOrdreCompra> findByOrdreCompra_Id(@Param("id_ordre") Long id_ordre);

    
}
