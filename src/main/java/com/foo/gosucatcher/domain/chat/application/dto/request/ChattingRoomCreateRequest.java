package com.foo.gosucatcher.domain.chat.application.dto.request;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

import com.foo.gosucatcher.domain.chat.application.ChattingService;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.MessageType;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;

@Getter
public class ChattingRoomCreateRequest {

	Long memberEstimateId;
	String roomUuid;
	static Set<WebSocketSession> sessions = new HashSet<>(); //후에 STOMP 적용하면서 삭제될 예정

	public ChattingRoomCreateRequest(Long memberEstimateId, String roomUuid) {
		this.memberEstimateId = memberEstimateId;
		this.roomUuid = roomUuid;
	}

	public static ChattingRoom toChattingRoom(MemberEstimate memberEstimate, String roomUuid) {

		return ChattingRoom.builder()
			.memberEstimate(memberEstimate)
			.roomUuid(roomUuid)
			.build();
	}

	public void handleActions(WebSocketSession session, MessageRequest messageRequest, ChattingService chattingService) {
		if (messageRequest.type().equals(MessageType.ENTER)) {
			sessions.add(session);
		}

		sendMessage(messageRequest, chattingService);
	}

	private <T> void sendMessage(T message, ChattingService chattingService) {
		sessions.parallelStream()
			.forEach(session -> chattingService.sendMessage(session, message));
	}
}
