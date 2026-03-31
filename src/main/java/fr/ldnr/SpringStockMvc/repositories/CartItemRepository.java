package fr.ldnr.SpringStockMvc.repositories;

import fr.ldnr.SpringStockMvc.entities.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

  Optional<CartItem> findByCartIdAndArticleId(Long id, Long articleId);
}
