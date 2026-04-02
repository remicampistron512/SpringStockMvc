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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ArticleController {

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @GetMapping({"/", "/index"})
  public String index(
      Model model,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "keyword", defaultValue = "") String kw,
      @RequestParam(name = "categoryId", defaultValue = "0") long categoryId) {

    Page<Article> articles;

    if (categoryId != 0) {
      articles = articleRepository.findByCategoryId(categoryId, PageRequest.of(page, 5));

      Category currentCategory = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found"));

      model.addAttribute("currentCategoryName", currentCategory.getName());
    } else {
      articles = articleRepository.findByDescriptionContains(kw, PageRequest.of(page, 5));
    }

    List<Category> categories = categoryRepository.findAll();

    model.addAttribute("listArticle", articles.getContent());
    model.addAttribute("listCategories", categories);
    model.addAttribute("pages", new int[articles.getTotalPages()]);
    model.addAttribute("categoryId", categoryId);
    model.addAttribute("currentPage", page);
    model.addAttribute("keyword", kw);

    return "articles";
  }

  @GetMapping("/article")
  public String articleForm(Model model) {
    model.addAttribute("article", new Article());
    model.addAttribute("listCategories", categoryRepository.findAll());
    return "article";
  }

  @GetMapping("/editArticle")
  public String editArticle(@RequestParam("id") Long id, Model model) {
    Article article = articleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Article introuvable"));

    model.addAttribute("article", article);
    model.addAttribute("listCategories", categoryRepository.findAll());
    return "editArticle";
  }

  @PostMapping("/save")
  public String save(
      @Valid Article article,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("listCategories", categoryRepository.findAll());
      return article.getId() == null ? "article" : "editArticle";
    }

    articleRepository.save(article);
    redirectAttributes.addFlashAttribute(
        "successMessage",
        article.getId() == null ? "Article enregistré" : "Article modifié"
    );

    return "redirect:/index";
  }

  @GetMapping("/delete")
  public String delete(
      @RequestParam Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "") String keyword,
      RedirectAttributes redirectAttributes) {

    articleRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "Article supprimé");
    return "redirect:/index?page=" + page + "&keyword=" + keyword;
  }
}