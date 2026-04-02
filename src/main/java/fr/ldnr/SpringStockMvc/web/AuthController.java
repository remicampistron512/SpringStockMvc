package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.User;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/login")
  public String processLogin(
      @RequestParam String username,
      @RequestParam String password,
      RedirectAttributes redirectAttributes) {

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
    return "redirect:/index";
  }
}