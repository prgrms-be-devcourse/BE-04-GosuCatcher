package com.foo.gosucatcher.domain.chat.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.foo.gosucatcher.domain.chat.application.MessageService;
import com.foo.gosucatcher.domain.chat.application.dto.request.MessageRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {

	private final SimpMessagingTemplate template;
	private final MessageService messageService;

	@MessageMapping("/message")
	public void sendMessage(MessageRequest messageRequest) {
		messageService.create(messageRequest);
		template.convertAndSend("/sub/chat/room/" + messageRequest.chattingRoomId(), messageRequest);
	}
}
