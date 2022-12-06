package org.ajou.multichatserver.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.ajou.multichatserver.user.domain.User;

@Getter
@Builder
public class SignUpResponse {

  private final Long userId;

  private final String email;

  private final String name;

  public static SignUpResponse from(User user) {
    return SignUpResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .build();
  }
}
