package group2.projecte2.repositori.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import group2.projecte2.model.Client;
import group2.projecte2.model.Proveidor;

public interface ProveidorRepositori extends JpaRepository<Proveidor, Long> {
List<Proveidor> findByNomContainingIgnoreCase(@Param("nombre") String nombre);
}
