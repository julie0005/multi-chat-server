package org.ajou.multichatserver.chat.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static org.ajou.multichatserver.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ajou.multichatserver.channel.domain.Channel;
import org.ajou.multichatserver.common.BaseEntity;
import org.ajou.multichatserver.common.exception.InvalidRequestException;
import org.ajou.multichatserver.user.domain.User;

@Entity
@Table(name = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Lob
  private String content;

  @Builder
  public ChatMessage(User sender, Channel channel, String content) {
    if (!hasText(content)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    this.sender = sender;
    this.channel = channel;
    this.content = content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "chatId : "+this.id+"\nsender : "+this.sender.getId()+"\nchannel : "+this.channel.getId()+"\ncontent : "+this.content+"\n";
  }
}
