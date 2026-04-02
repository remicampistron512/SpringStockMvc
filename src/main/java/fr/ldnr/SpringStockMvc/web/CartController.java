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

  @PostMapping("/addToCart2")
  public String addToCart2(@RequestParam Long articleId,
      @RequestParam(defaultValue = "1") int quantity) {

    /*String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseGet(() -> {
          Cart newCart = new Cart();
          newCart.setUser(user);
          return cartRepository.save(newCart);
        });

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new RuntimeException("Article not found"));

    CartItem cartItem = cartItemRepository.findByCartIdAndArticleId(cart.getId(), articleId)
        .orElseGet(() -> {
          CartItem item = new CartItem();
          item.setCart(cart);
          item.setArticle(article);
          item.setQuantity(0);
          item.setUnitPrice(article.getPrice());
          return item;
        });

    cartItem.setQuantity(cartItem.getQuantity() + quantity);
    cartItemRepository.save(cartItem);

    return "redirect:/cart";*/

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new RuntimeException("Article not found"));

    for (int i = 0; i < quantity; i++) {
      cartService.addArticle(article);
    }

    return "redirect:/index";
  }
  @GetMapping("/cart")
  public String getCart(Model model) {
    model.addAttribute("articles", cartService.getArticles());
    return "cart";
  }
}