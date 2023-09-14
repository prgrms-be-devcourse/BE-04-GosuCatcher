package com.foo.gosucatcher.domain.chat.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.chat.application.ChattingService;
import com.foo.gosucatcher.domain.chat.application.dto.request.ChattingRoomCreateRequest;
import com.foo.gosucatcher.domain.chat.application.dto.request.MessageRequest;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

	private final ObjectMapper objectMapper;
	private final ChattingService chattingService;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("payload {}", payload);

		MessageRequest chatMessage = objectMapper.readValue(payload, MessageRequest.class);
		log.info("chattingRoomId={}", chatMessage.chattingRoomId());
		log.info("senderId={}", chatMessage.senderId());
		log.info("type={}", chatMessage.type());
		log.info("content={}", chatMessage.content());

		ChattingRoom room = chattingService.findRoomById(chatMessage.chattingRoomId().toString());
		ChattingRoomCreateRequest chattingRoomCreateRequest = new ChattingRoomCreateRequest(1L, room.getRoomUuid());

		chattingRoomCreateRequest.handleActions(session, chatMessage, chattingService);
	}
}
