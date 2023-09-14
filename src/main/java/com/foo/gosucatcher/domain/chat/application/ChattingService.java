package com.foo.gosucatcher.domain.chat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingService {

	private final ObjectMapper objectMapper;
	private Map<String, ChattingRoom> chatRooms;

	@PostConstruct
	private void init() {
		chatRooms = new LinkedHashMap<>();
	}

	public List<ChattingRoom> findAllRoom() {
		return new ArrayList<>(chatRooms.values());
	}

	public ChattingRoom findRoomById(String roomId) {
		return chatRooms.get(roomId);
	}

	public ChattingRoom createRoom() {
		String randomId = UUID.randomUUID().toString();
		ChattingRoom chatRoom = ChattingRoom.builder()
			.roomUuid(randomId)
			.build();
		chatRooms.put(randomId, chatRoom);
		return chatRoom;
	}

	public <T> void sendMessage(WebSocketSession session, T message) {
		try{
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
