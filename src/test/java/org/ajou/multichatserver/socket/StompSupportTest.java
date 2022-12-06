package org.ajou.multichatserver.socket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ajou.multichatserver.channel.domain.Channel;
import org.ajou.multichatserver.channel.service.ChannelService;
import org.ajou.multichatserver.chat.domain.ChatMessage;
import org.ajou.multichatserver.chat.dto.ChatRequest;
import org.ajou.multichatserver.chat.dto.ChatResponse;
import org.ajou.multichatserver.chat.service.ChatService;
import org.ajou.multichatserver.jwt.JwtAuthenticationToken;
import org.ajou.multichatserver.jwt.JwtPrincipal;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.dto.request.UserSignUpRequest;
import org.ajou.multichatserver.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class StompSupportTest {
    protected StompSession stompSession;

    @LocalServerPort
    private int port;

    private static final String url = "ws://localhost:";

    private final ChannelService channelService;

    private final WebSocketStompClient websocketClient;

    private final UserService userService;

    private final ChatService chatService;
    private User testUser;

    private final String testPassword = "test1234!";

    private final AuthenticationManager authenticationManager;

    private Channel testChannel;

    private final ObjectMapper objectMapper;

    @Autowired
    public StompSupportTest(ChannelService channelService, UserService userService, ChatService chatService,
                            AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.channelService = channelService;
        this.userService = userService;
        this.chatService = chatService;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.websocketClient = new WebSocketStompClient(new SockJsClient(createTransport()));
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        this.websocketClient.setMessageConverter(messageConverter);
    }

    @BeforeAll
    public void initializeObjects() {
        testUser = userService.signUp(UserSignUpRequest.builder()
                .email("test@gmail.com")
                .name("김승은")
                .password(testPassword)
                .build());
        testChannel = channelService.createChannel("general");
    }

    @BeforeEach
    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(testUser.getEmail(), testPassword);
        Authentication authentication = authenticationManager.authenticate(authToken);
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        StompHeaders headers = new StompHeaders();
        headers.add("accessToken", principal.getAccessToken());
        this.stompSession = this.websocketClient.connectAsync(url + port + "/api/socket/chat", new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);
    }

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()) {
            this.stompSession.disconnect();
        }
    }

    private List<Transport> createTransport() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @Test
    @DisplayName("소켓 연결 후 stompCli publish, subscribe 통신 테스트")
    public void testChat() throws ExecutionException, InterruptedException, TimeoutException {
        /* GIVEN */
        MessageFrameHandler<ChatResponse> handler = new MessageFrameHandler<>(ChatResponse.class);
        String content = "Hello motherfuckers!";
        String destination = "/sub/chat/message/channel/"+testChannel.getId();
        this.stompSession.subscribe(destination, handler);
        ChatRequest request = ChatRequest.builder()
                .channelId(testChannel.getId())
                .senderId(testUser.getId())
                .content(content)
                .build();
        /* WHEN */
        this.stompSession.send("/pub/chat/message", request);

        /* THEN */
        ChatResponse response = handler.getCompletableFuture().get(10, TimeUnit.SECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getSenderName()).isEqualTo(testUser.getName());
    }
    @Test
    @DisplayName("채팅 송신 후 저장 확인 테스트")
    public void testChatSaved() throws ExecutionException, InterruptedException, TimeoutException {
        /* GIVEN */
        MessageFrameHandler<ChatResponse> handler = new MessageFrameHandler<>(ChatResponse.class);
        String content = "Hello motherfuckers!";
        String destination = "/sub/chat/message/channel/"+testChannel.getId();
        this.stompSession.subscribe(destination, handler);
        ChatRequest request = ChatRequest.builder()
                .channelId(testChannel.getId())
                .senderId(testUser.getId())
                .content(content)
                .build();
        /* WHEN */
        this.stompSession.send("/pub/chat/message", request);

        handler.getCompletableFuture().get(10, TimeUnit.SECONDS);
        /* THEN */
        List<ChatMessage> messages = chatService.getAllChatMessages();
        assertThat(messages.size()).isEqualTo(1);
        assertThat(messages.get(0).getContent()).isEqualTo(content);
    }

}