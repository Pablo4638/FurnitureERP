package group2.projecte2.repositori.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import group2.projecte2.model.Empleat;

public interface EmpleatRepositori extends JpaRepository<Empleat, Long> {
    Empleat findByemail(String email);

    boolean existsByEmail(String email);

    Optional<Empleat> findByChatId(String chatId);

    Optional<Empleat> findByEmail(String email);

}
