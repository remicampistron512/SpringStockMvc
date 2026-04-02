package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.Article;
import fr.ldnr.SpringStockMvc.repositories.ArticleRepository;
import fr.ldnr.SpringStockMvc.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private CartService cartService;

  @PostMapping("/addToCart")
  public String addToCart(
      @RequestParam Long articleId,
      @RequestParam(defaultValue = "1") int quantity,
      RedirectAttributes redirectAttributes) {

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new RuntimeException("Article not found"));

    for (int i = 0; i < quantity; i++) {
      cartService.addArticle(article);
    }

    redirectAttributes.addFlashAttribute("successMessage", "Article ajouté au panier");
    return "redirect:/index";
  }

  @PostMapping("/removeFromCart")
  public String removeFromCart(
      @RequestParam Long articleId,
      RedirectAttributes redirectAttributes) {

    try {
      boolean removed = cartService.removeArticleById(articleId);

      if (removed) {
        redirectAttributes.addFlashAttribute("successMessage", "Article supprimé du panier");
        System.out.println("Article supprimé du panier");
      } else {
        redirectAttributes.addFlashAttribute("errorMessage", "Article non présent dans le panier");
        System.out.println("Article non présent dans le panier");
      }

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression");
      e.printStackTrace();
    }

    return "redirect:/cart";
  }

  @GetMapping("/cart")
  public String getCart(Model model) {
    model.addAttribute("articles", cartService.getArticles());

    double total = cartService.getArticles()
        .stream()
        .mapToDouble(Article::getPrice)
        .sum();

    model.addAttribute("total", total);

    return "cart";
  }
}