package group2.projecte2.repositori.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import group2.projecte2.model.CategoriaProducte;

public interface CategoriaRepositori extends JpaRepository<CategoriaProducte, Long> {
    
}
