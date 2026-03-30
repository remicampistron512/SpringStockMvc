package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.Article;
import fr.ldnr.SpringStockMvc.entities.Category;
import fr.ldnr.SpringStockMvc.repositories.ArticleRepository;
import fr.ldnr.SpringStockMvc.repositories.CategoryRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ArticleController {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    //@RequestMappingValue(value="/index" , method=RequestMethod.GET)
    @GetMapping("/index")
    public String index(Model model, @RequestParam(name="page",defaultValue = "0") int page,
                                     @RequestParam(name="keyword", defaultValue = "") String kw){
      Page<Article> articles = articleRepository.findByDescriptionContains(kw,PageRequest.of(page,5));
      List<Category> categories = categoryRepository.findAll();
      model.addAttribute("listArticle",articles.getContent());
      model.addAttribute("listCategories",categories);
      model.addAttribute("pages", new int[articles.getTotalPages()]);

      model.addAttribute("currentPage",page);

      model.addAttribute("keyword",kw);

      return "articles";
    }

    @GetMapping("/delete")
    public String delete(Long id, int page, String keyword){
      articleRepository.deleteById(id);

      return "redirect:/index?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/article")
    public String article(Model model){
      model.addAttribute("article", new Article());
      return "article";
    }

    @PostMapping("/save")
    public String save(@Valid Article article, BindingResult bindingResult){
      if(bindingResult.hasErrors()) return "article";
      articleRepository.save(article);
      return "redirect:/index";
    }
}
