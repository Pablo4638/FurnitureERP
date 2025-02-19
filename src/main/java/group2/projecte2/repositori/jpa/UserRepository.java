package group2.projecte2.repositori.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group2.projecte2.model.Empleat;
import group2.projecte2.model.User;
import group2.projecte2.model.Enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    public User getUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);

    Optional<User> findByUsername(String username);

    @Query("SELECT e FROM Empleat e LEFT JOIN User u ON e.id = u.empleat.id WHERE u.empleat.id IS NULL")
    List<Empleat> EmpleatSenseUsuari();

    boolean existsByUsername(String username);
}