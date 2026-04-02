package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.User;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/users")
  public String manageUsers(Model model) {
    List<User> users = userRepository.findAll();
    model.addAttribute("users", users);
    return "users";
  }

  @GetMapping("/createUser")
  public String createUser(Model model) {
    model.addAttribute("user", new User());
    return "createUser";
  }

  @PostMapping("/saveUser")
  public String saveUser(
      @Valid User user,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("user", user);
      return "createUser";
    }

    userRepository.save(user);
    redirectAttributes.addFlashAttribute("successMessage", "Utilisateur enregistré");
    return "redirect:/index";
  }

  @GetMapping("/editUser")
  public String editUser(
      @RequestParam("id") Long id,
      Model model,
      RedirectAttributes redirectAttributes) {

    User user = userRepository.findById(id).orElse(null);

    if (user == null) {
      redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable");
      return "redirect:/users";
    }

    model.addAttribute("user", user);
    return "editUser";
  }

  @GetMapping("/deleteUser")
  public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    userRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé");
    return "redirect:/users";
  }
}