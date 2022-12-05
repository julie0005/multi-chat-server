package org.ajou.multichatserver.common.interceptor;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajou.multichatserver.jwt.JwtPrincipal;
import org.ajou.multichatserver.jwt.JwtService;
import org.ajou.multichatserver.jwt.claims.AccessClaim;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientInboundChannelInterceptor implements ChannelInterceptor {

    private final UserService userService;

    private final JwtService jwtService;


    @Override
    public Message<?> preSend(Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (Objects.nonNull(accessor)) {
            log.info("INCOMING {}", accessor.getDetailedLogMessage(message.getPayload()));
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                message = this.connect(message, accessor);
            }
        }
        return message;
    }

    private Message<?> connect(Message<?> message, final StompHeaderAccessor accessor) {

        final String accessToken = accessor.getFirstNativeHeader("accessToken");
        AccessClaim claim = jwtService.verifyAccessToken(accessToken);
        User user = userService.getUserById(claim.getUserId());
        accessor.setUser(new JwtPrincipal(accessToken, user));

        return message;
    }
}
