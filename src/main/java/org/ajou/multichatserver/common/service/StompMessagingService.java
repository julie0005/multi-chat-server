package org.ajou.multichatserver.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class StompMessagingService {

    private static final String CHAT_DESTINATION_PREFIX = "/sub/chat/message/channel/";
    private final SimpMessagingTemplate messagingTemplate;

    public <D> void sendToChannel(Long channelId, D message) {
        messagingTemplate.convertAndSend(CHAT_DESTINATION_PREFIX+channelId, message);
    }

}
