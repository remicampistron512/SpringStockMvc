package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.Article;
import fr.ldnr.SpringStockMvc.entities.Category;
import fr.ldnr.SpringStockMvc.entities.User;
import fr.ldnr.SpringStockMvc.repositories.ArticleRepository;
import fr.ldnr.SpringStockMvc.repositories.CartItemRepository;
import fr.ldnr.SpringStockMvc.repositories.CartRepository;
import fr.ldnr.SpringStockMvc.repositories.CategoryRepository;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import fr.ldnr.SpringStockMvc.services.CartService;
import fr.ldnr.SpringStockMvc.services.UserService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @Autowired
  UserService userService;

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
  public String delete(Long id, int page, String keyword, RedirectAttributes redirectAttributes ) {
    articleRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "Article supprimé");
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
      @RequestParam(defaultValue = "1") int quantity,RedirectAttributes redirectAttributes) {

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new RuntimeException("Article not found"));

    for (int i = 0; i < quantity; i++) {
      cartService.addArticle(article);
    }
    redirectAttributes.addFlashAttribute("successMessage", "Article ajouté au panier");
    return "redirect:/index";
  }

  @PostMapping("/login")
  public String processLogin(@RequestParam String username,
      @RequestParam String password,RedirectAttributes redirectAttributes) {

    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable");
      return "redirect:/login";
    }
    User user = userOpt.get();

    if (!password.equals(user.getPassword())) {
      redirectAttributes.addFlashAttribute("errorMessage", "Mot de passe incorrect");
      return "redirect:/login";
    }

    redirectAttributes.addFlashAttribute("successMessage", "Connexion réussie");
    return "redirect:/home";
  }

  @GetMapping("/login")
  public String login(){
    return "login";
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


  @PostMapping("/save")
  public String save(@Valid Article article, BindingResult bindingResult,RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "article";
    }
    redirectAttributes.addFlashAttribute("successMessage", "Article modifié");
    articleRepository.save(article);
    return "redirect:/index";
  }

  @GetMapping("/editUser")
  public String editUser(@RequestParam("id") Long id, Model model,RedirectAttributes redirectAttributes) {


    Optional<User> user = userRepository.findById(id);

    if (user.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable");
      return "/editUser.id="+id;
    }
    model.addAttribute("user", user);

    return "editUser";
  }
  @GetMapping("/deleteUser")
  public String deleteUser (Long id,RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé");
    userRepository.deleteById(id);
    return "redirect:/users";

  }
  @GetMapping("/users")
  public String manageUsers(Model model){
    List<User> users = userRepository.findAll();
    model.addAttribute("users", users);
    return ("users");
  }
  @GetMapping("/createUser")
  public String createUser(Model model){
    return ("createUser");
  }
  @PostMapping("/saveUser")
  public String saveUser(@Valid User user, BindingResult bindingResult,RedirectAttributes redirectAttributes){
    redirectAttributes.addFlashAttribute("successMessage", "Utilisateur enregistré");
    userRepository.save(user);
    return "redirect:/index";

  }
}
