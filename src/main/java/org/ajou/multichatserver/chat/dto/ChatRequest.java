package org.ajou.multichatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

  private Long senderId;

  private Long channelId;

  private String content;

  @Override
  public String toString() {
    return "senderId : "+senderId+"\nchannelId : "+channelId+"\ncontent : "+content;
  }
}
