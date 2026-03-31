package fr.ldnr.SpringStockMvc.repositories;

import fr.ldnr.SpringStockMvc.entities.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

  Optional<Cart> findByUserId(Long id);
}
