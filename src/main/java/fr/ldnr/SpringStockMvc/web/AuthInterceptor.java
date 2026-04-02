package fr.ldnr.SpringStockMvc.web;

import fr.ldnr.SpringStockMvc.entities.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor responsible for handling authentication checks.
 * <p>
 * This interceptor ensures that protected routes are only accessible
 * to authenticated users. If no user is found in the session,
 * the request is redirected to the login page.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

  /**
   * Intercepts incoming HTTP requests before they reach the controller.
   * <p>
   * - Allows access to public routes (login, static resources, etc.)
   * - Checks if a user is stored in the session for protected routes
   * - Redirects unauthenticated users to the login page
   *
   * @param request  the current HTTP request
   * @param response the current HTTP response
   * @param handler  the chosen handler to execute (controller)
   * @return {@code true} to continue processing, {@code false} to block the request
   * @throws Exception if an error occurs during processing
   */
  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {

    String uri = request.getRequestURI();

    // Allow access to public routes and static resources without authentication
    if (uri.equals("/")
        || uri.equals("/index")
        || uri.equals("/login")
        || uri.equals("/logout")
        || uri.startsWith("/css/")
        || uri.startsWith("/js/")
        || uri.startsWith("/images/")) {
      return true;
    }

    // Retrieve existing session without creating a new one
    HttpSession session = request.getSession(false);

    // Extract the logged user from the session (if any)
    SessionUser user = (session == null)
        ? null
        : (SessionUser) session.getAttribute("loggedUser");

    // If no user is found, redirect to login page and block request
    if (user == null) {
      response.sendRedirect("/login");
      return false;
    }

    // User is authenticated → allow request to proceed
    return true;
  }
}