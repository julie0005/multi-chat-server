package org.ajou.multichatserver.chat.controller;

import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.chat.dto.ChatRequest;
import org.ajou.multichatserver.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatRequest request) {
        chatService.sendMessage(request);
    }
}
