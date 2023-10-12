package com.github.raphaelfontoura.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.raphaelfontoura.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FilterTaskAuth extends OncePerRequestFilter {

  private final IUserRepository userRepository;

  @Value("${requestparam.userid}")
  private String headerUserId;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks")) {
      var authRequest = request.getHeader("Authorization");
      var authString = authBasicRequestHandle(authRequest);
      var credentials = authString.split(":");
      var username = credentials[0];
      var password = credentials[1];

      var user = userRepository.findByUsername(username);
      if (user == null) {
        response.sendError(401, "Usuário sem autorização");
      } else {
        var result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (result.verified) {
          request.setAttribute(headerUserId, user.getId());
          filterChain.doFilter(request, response);
        } else {
          response.sendError(401, "Usuário sem autorização");
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }

  }

  private String authBasicRequestHandle(String authRequest) {
    var authEncoded = authRequest.substring("Basic".length()).trim();
    var authDecoded = Base64.getDecoder().decode(authEncoded);

    return new String(authDecoded);
  }

}
