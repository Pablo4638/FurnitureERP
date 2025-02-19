package group2.projecte2.repositori.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group2.projecte2.model.DetallComanda;

public interface DetallComandaRepositori extends JpaRepository<DetallComanda, Long> {

   @Query("SELECT det FROM DetallComanda det WHERE det.comanda.id = :id_comanda")
    List<DetallComanda> findByComanda_Id(@Param("id_comanda") Long id_comanda);
}