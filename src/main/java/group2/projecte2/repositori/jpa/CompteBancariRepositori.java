package group2.projecte2.repositori.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group2.projecte2.model.CompteBancari;

@Repository
public interface CompteBancariRepositori extends JpaRepository<CompteBancari, Long> {

}
