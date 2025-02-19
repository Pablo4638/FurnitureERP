package group2.projecte2.repositori.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import group2.projecte2.model.MovimentInventari;
import group2.projecte2.model.Enums.TipusMoviment;

public interface MovimentInventariRepositori extends JpaRepository<MovimentInventari, Long> {

    @Query("SELECT m FROM MovimentInventari m WHERE m.tipus_moviment = :tipusMoviment")
    List<MovimentInventari> findByTipusMoviment(TipusMoviment tipusMoviment);
}
