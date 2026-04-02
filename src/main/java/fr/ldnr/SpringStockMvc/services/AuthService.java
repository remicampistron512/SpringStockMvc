package fr.ldnr.SpringStockMvc.services;

import fr.ldnr.SpringStockMvc.entities.User;
import fr.ldnr.SpringStockMvc.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User authenticate(String username, String password) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

    if (!password.equals(user.getPassword())) {
      throw new BadCredentialsException("Mot de passe incorrect");
    }

    return user;
  }
}