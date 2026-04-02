package fr.ldnr.SpringStockMvc.services;

import fr.ldnr.SpringStockMvc.entities.Article;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartService {
  private final List<Article> articles = new ArrayList<>();

  public void addArticle(Article article) {
    articles.add(article);
  }

  public List<Article> getArticles() {
    return articles;
  }

  public void clear() {
    articles.clear();
  }

  public boolean removeArticleById(Long articleId) {
    for (Article article : articles) {
      if (article.getId() != null && article.getId().equals(articleId)) {
        articles.remove(article);
        return true;
      }
    }
    return false;
  }
}