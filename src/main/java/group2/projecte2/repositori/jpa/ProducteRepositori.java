package group2.projecte2.repositori.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import group2.projecte2.model.Producte;
import group2.projecte2.model.Proveidor;

public interface ProducteRepositori extends JpaRepository<Producte, Long> {

  List<Producte> findByProveidor(Proveidor proveidor);
  @Query("SELECT p FROM Producte p WHERE p.nom_producte LIKE %:nombre%")
  List<Producte> findByNomProducteContaining(@Param("nombre") String nombre);

}
