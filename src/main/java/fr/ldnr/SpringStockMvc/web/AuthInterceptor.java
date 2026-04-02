package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.SessionUser;
import fr.ldnr.SpringStockMvc.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {

    String uri = request.getRequestURI();

    // Public routes
    if (uri.equals("/")
        || uri.equals("/index")
        || uri.equals("/login")
        || uri.equals("/logout")
        || uri.startsWith("/css/")
        || uri.startsWith("/js/")
        || uri.startsWith("/images/")) {
      return true;
    }

    HttpSession session = request.getSession(false);
    SessionUser user = (session == null)
        ? null
        : (SessionUser) session.getAttribute("loggedUser");

    if (user == null) {
      response.sendRedirect("/login");
      return false;
    }

    return true;
  }
}