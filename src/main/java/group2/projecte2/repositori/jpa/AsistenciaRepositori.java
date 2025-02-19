package group2.projecte2.repositori.jpa;    

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group2.projecte2.model.Asistencia;
public interface AsistenciaRepositori extends JpaRepository<Asistencia, Long> {

    @Query(value = "SELECT * FROM asistencia a WHERE a.id_empleat = :idEmpleat", nativeQuery = true)
    List<Asistencia> findByEmpleatIdEmpleat(@Param("idEmpleat") Long idEmpleat);

    @Query(value = "SELECT * FROM asistencia WHERE id_empleat = :id_empleat AND hora_sortida IS NULL ORDER BY hora_entrada DESC LIMIT 1", nativeQuery = true)
    Optional<Asistencia> findUltimaAsistenciaPorEmpleado(@Param("id_empleat") Long empleatId);

}
