package org.ajou.multichatserver.chat.service;

import static org.ajou.multichatserver.common.ErrorCode.CHAT_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.channel.domain.Channel;
import org.ajou.multichatserver.channel.service.ChannelService;
import org.ajou.multichatserver.chat.domain.ChatMessage;
import org.ajou.multichatserver.chat.dto.ChatRequest;
import org.ajou.multichatserver.chat.dto.ChatResponse;
import org.ajou.multichatserver.chat.repository.ChatRepository;
import org.ajou.multichatserver.common.exception.NoSuchElementException;
import org.ajou.multichatserver.common.service.RedisService;
import org.ajou.multichatserver.config.RedisConfig;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final UserService userService;

  private final ChannelService channelService;

  private final ChatRepository chatRepository;

  private final ChannelTopic chatTopic = new ChannelTopic(RedisConfig.CHAT_TOPIC_NAME);

  private final RedisService redisService;
  @Transactional
  public void sendMessage(ChatRequest request) {
    ChatMessage message = createChatMessage(request);
    ChatResponse response = ChatResponse.from(message);
    redisService.publishChatMessage(chatTopic, response);
  }

  @Transactional
  public ChatMessage createChatMessage (ChatRequest chatRequest) {
    User sender = userService.getUserById(chatRequest.getSenderId());
    Channel channel = channelService.getChannelById(chatRequest.getChannelId());
    ChatMessage chatMessage = ChatMessage.builder()
        .sender(sender)
        .channel(channel)
        .content(chatRequest.getContent())
        .build();
    return chatRepository.save(chatMessage);
  }

  @Transactional(readOnly = true)
  public Page<ChatResponse> getChatMessages(Long channelId, Pageable pageable) {
    channelService.validateChannelId(channelId);
    Page<ChatMessage> chatMessages = chatRepository.findChatMessageByChannelId(channelId, pageable);
    return chatMessages.map(ChatResponse::from);
  }

  @Transactional(readOnly = true)
  public ChatMessage getChatMessageById(Long messageId) {
    return chatRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException(CHAT_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<ChatMessage> getAllChatMessages() {
    return chatRepository.findAll();
  }
}
