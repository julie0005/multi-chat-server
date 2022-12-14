package org.ajou.multichatserver.jwt;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.common.exception.AuthErrorException;
import org.ajou.multichatserver.jwt.claims.AccessClaim;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final String accessHeaderKey;

  private final JwtService jwtService;

  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = getAccessToken(request);
      if (nonNull(token)) {
        try {
          AccessClaim claims = jwtService.verifyAccessToken(token);
          Long userId = claims.getUserId();
          List<GrantedAuthority> authorities = getAuthorities(claims);
          User currentUser = userService.getUserById(userId);
          if (!isNull(userId) && authorities.size() > 0) {
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtPrincipal(token, currentUser), null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (TokenExpiredException e) {
          log.warn("????????? ????????? ???????????????. token: {}", token);
          throw e;
        } catch (AuthErrorException e) {
          log.warn("???????????? ????????? ???????????????. token: {}", token);
          throw e;
        } catch (Exception e) {
          log.warn("Jwt ?????? ??????: {}, class: {}", e.getMessage(), e.getClass());
          throw e;
        }
      }
    } else {
      log.debug("SecurityContextHolder??? ?????? authentication ????????? ????????? ????????????.: '{}'", SecurityContextHolder.getContext().getAuthentication());
    }
    chain.doFilter(request, response);
  }

  private String getAccessToken(HttpServletRequest request) {
    String token = request.getHeader(accessHeaderKey);
    if(hasText(token)) {
      log.debug("Jwt authorization api detected: {}", token);
      return URLDecoder.decode(token, StandardCharsets.UTF_8);
    }
    return null;
  }

  private List<GrantedAuthority> getAuthorities(AccessClaim claims) {
    String[] roles = claims.getRoles();
    return roles == null || roles.length == 0 ? Collections.emptyList() : Arrays.stream(roles).map(
        SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
