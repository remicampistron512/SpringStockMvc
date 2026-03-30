package fr.ldnr.SpringStockMvc.repositories;


import fr.ldnr.SpringStockMvc.entities.Article;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article,Long> {

}
