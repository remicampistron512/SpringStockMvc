package fr.ldnr.SpringStockMvc;

import fr.ldnr.SpringStockMvc.entities.Article;
import fr.ldnr.SpringStockMvc.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringStockMvcApplication implements CommandLineRunner {
  @Autowired
  ArticleRepository articleRepository;
	public static void main(String[] args) {
		SpringApplication.run(SpringStockMvcApplication.class, args);
	}

  @Override
  public void run(String...args) throws Exception {
    //articleRepository.save(new Article(null, "Samsung S8",250));
    //articleRepository.save(new Article(null, "Samsung S9",300));
    //articleRepository.save(new Article(null, "Samsung S10",500));
  }
}
