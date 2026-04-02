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

  /**
   * Controller responsible for handling articles
   * <p>
   * This controller allows  to:
   * <ul>
   *     <li>Display all articles</li>
   *     <li>Add an article</li>
   *     <li>Edit an article</li>
   *     <li>Save an article</li>
   *     <li>Delete an article</li>
   * </ul>
   */

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * Display all articles
   * @param model data to pass to the view
   * @param page pager handling
   * @param kw keyword filtering
   * @param categoryId category filtering
   * @return the articles view
   */
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

  /**
   * Display the article form
   * @param model data to pass to the view
   * @return the article view
   */
  @GetMapping("/article")
  public String articleForm(Model model) {
    model.addAttribute("article", new Article());
    model.addAttribute("listCategories", categoryRepository.findAll());
    return "article";
  }

  /**
   * Form to edit an article
   * @param id the article id
   * @param model data to pass to the view
   * @return
   */
  @GetMapping("/editArticle")
  public String editArticle(@RequestParam("id") Long id, Model model) {
    Article article = articleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Article introuvable"));

    model.addAttribute("article", article);
    model.addAttribute("listCategories", categoryRepository.findAll());
    return "editArticle";
  }

  /**
   * Handles the creation or update of an article.
   * <p>
   * If validation errors are present, the form is redisplayed with errors.
   * Otherwise, the article is saved in the database and the user is redirected
   * to the index page with a success message.
   *
   * @param article the article entity populated from the form
   * @param bindingResult contains validation errors (if any)
   * @param model used to pass data back to the view in case of validation errors
   * @param redirectAttributes used to pass success messages after redirect
   * @return the form view ("article" or "editArticle") if validation fails,
   *         otherwise a redirect to the index page
   */
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

  /**
   * Deletes an article by its identifier.
   * <p>
   * After deletion, the user is redirected back to the index page while
   * preserving pagination and search keyword parameters.
   *
   * @param id the identifier of the article to delete
   * @param page the current page number (used to maintain pagination after redirect)
   * @param keyword the current search keyword (used to maintain filtering after redirect)
   * @param redirectAttributes used to pass a success message after redirect
   * @return a redirect to the index page with pagination and filtering parameters
   */
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