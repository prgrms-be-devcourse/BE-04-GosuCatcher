package com.foo.gosucatcher.domain.chat.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.MessageService;
import com.foo.gosucatcher.domain.chat.application.dto.request.MessageRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

	private final ObjectMapper objectMapper;
	private final ChattingRoomService chattingRoomService;
	private final MessageService messageService;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();

		MessageRequest messageRequest = objectMapper.readValue(payload, MessageRequest.class);

		chattingRoomService.handleWebSocketSession(session, messageRequest);
		messageService.create(messageRequest.senderId(), messageRequest.chattingRoomId(), messageRequest.content(), messageRequest.chattingStatus());
	}
}
