package fr.ldnr.SpringStockMvc.repositories;

import fr.ldnr.SpringStockMvc.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

  Optional<User> findByUsername(String username);
}
