package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.Article;
import fr.ldnr.SpringStockMvc.entities.Category;
import fr.ldnr.SpringStockMvc.repositories.ArticleRepository;
import fr.ldnr.SpringStockMvc.repositories.CartItemRepository;
import fr.ldnr.SpringStockMvc.repositories.CartRepository;
import fr.ldnr.SpringStockMvc.repositories.CategoryRepository;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import fr.ldnr.SpringStockMvc.services.CartService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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

  @Autowired
  CartItemRepository cartItemRepository;

  @Autowired
  CartRepository cartRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private CartService cartService;

  //@RequestMappingValue(value="/index" , method=RequestMethod.GET)
  @GetMapping({"/","/index"})
  public String index(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "keyword", defaultValue = "") String kw,
      @RequestParam(name = "categoryId", defaultValue = "0") long categoryId) {
    Page<Article> articles;
    if (categoryId != 0) {
      articles = articleRepository.findByCategoryId(categoryId, PageRequest.of(page, 5));
      Category currentCategory = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Article not found"));
      String currentCategoryName = currentCategory.getName();
      model.addAttribute("currentCategoryName", currentCategoryName);
    } else {
      articles = articleRepository.findByDescriptionContains(kw,
          PageRequest.of(page, 5));
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


  @GetMapping("/delete")
  public String delete(Long id, int page, String keyword) {
    articleRepository.deleteById(id);

    return "redirect:/index?page=" + page + "&keyword=" + keyword;
  }

  @GetMapping("/article")
  public String article(Model model) {
    model.addAttribute("article", new Article());
    List<Category> categories = categoryRepository.findAll();
    model.addAttribute("listCategories", categories);
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

  @PostMapping("/addToCart")
  public String addToCart(@RequestParam Long articleId,
      @RequestParam(defaultValue = "1") int quantity) {

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new RuntimeException("Article not found"));

    for (int i = 0; i < quantity; i++) {
      cartService.addArticle(article);
    }

    return "redirect:/index";
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
  }

  @GetMapping("/cart")
  public String getCart(Model model) {
    model.addAttribute("articles", cartService.getArticles());
    return "cart";
  }


  @PostMapping("/save")
  public String save(@Valid Article article, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "article";
    }
    articleRepository.save(article);
    return "redirect:/index";
  }
}
