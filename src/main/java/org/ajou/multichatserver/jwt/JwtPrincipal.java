package org.ajou.multichatserver.jwt;

import static java.util.Objects.isNull;
import static org.ajou.multichatserver.common.ErrorCode.EMAIL_REQUIRED;
import static org.ajou.multichatserver.common.ErrorCode.USER_PARAM_REQUIRED;
import static org.springframework.util.StringUtils.hasText;

import java.security.Principal;
import lombok.Getter;
import org.ajou.multichatserver.common.exception.InvalidRequestException;
import org.ajou.multichatserver.user.domain.User;

@Getter
public class JwtPrincipal implements Principal {

  private final String accessToken;

  private final User user;

  public JwtPrincipal(String accessToken, User user) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(user)) throw new InvalidRequestException(USER_PARAM_REQUIRED);

    this.accessToken = accessToken;
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getId().toString();
  }
}
