package fr.ldnr.SpringStockMvc.repositories;

import fr.ldnr.SpringStockMvc.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
