package org.ajou.multichatserver.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ajou.multichatserver.chat.domain.ChatMessage;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

  private Long messageId;

  private String type;

  private String senderName;

  private Long channelId;

  private String content;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdAt;

  public static ChatResponse from(ChatMessage chatMessage) {
    return ChatResponse.builder()
        .messageId(chatMessage.getId())
        .channelId(chatMessage.getChannel().getId())
        .senderName(chatMessage.getSender().getName())
        .content(chatMessage.getContent())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }

  public void setContent(String content) {
    this.content = content;
  }
}
