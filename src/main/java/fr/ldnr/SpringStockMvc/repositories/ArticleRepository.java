package fr.ldnr.SpringStockMvc.repositories;


import fr.ldnr.SpringStockMvc.entities.Article;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article,Long> {
  Page<Article> findByDescriptionContains(String description, Pageable pageable);

  Page<Article> findByCategoryId(long categoryId, Pageable pageable);
}
