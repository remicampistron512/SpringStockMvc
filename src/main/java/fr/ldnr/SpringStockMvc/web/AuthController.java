package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.SessionUser;
import fr.ldnr.SpringStockMvc.entities.User;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import fr.ldnr.SpringStockMvc.services.AuthService;
import fr.ldnr.SpringStockMvc.services.BadCredentialsException;
import fr.ldnr.SpringStockMvc.services.UserNotFoundException;
import jakarta.servlet.http.HttpSession;
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

  public AuthController(AuthService authService) {
    this.authService = authService;
  }
  private final AuthService authService;
  @PostMapping("/login")
  public String processLogin(
      @RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    try {
      User user = authService.authenticate(username, password);

      SessionUser sessionUser = new SessionUser(
          user.getId(),
          user.getUsername(),
          user.getRole()
      );
      session.setAttribute("loggedUser", sessionUser);
      redirectAttributes.addFlashAttribute("successMessage", "Connexion réussie");
      return "redirect:/index";

    } catch (UserNotFoundException | BadCredentialsException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/login";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
    session.invalidate(); // destroys session completely
    redirectAttributes.addFlashAttribute("successMessage", "Déconnexion réussie");
    return "redirect:/login";
  }
}