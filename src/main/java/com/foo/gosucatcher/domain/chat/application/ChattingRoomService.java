package com.foo.gosucatcher.domain.chat.application;

import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_CHATTING_ROOM;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER_ESTIMATE;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.chat.application.dto.request.ChattingRoomRequest;
import com.foo.gosucatcher.domain.chat.application.dto.request.MessageRequest;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.chat.domain.ChattingStatus;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.aop.CurrentExpertId;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChattingRoomService {

	private final MemberEstimateRepository memberEstimateRepository;
	private final ChattingRoomRepository chattingRoomRepository;
	private final MemberRepository memberRepository;
	private final ObjectMapper objectMapper;
	private static Set<WebSocketSession> sessions = new HashSet<>(); //STOMP 적용하면서 삭제될 예정

	public ChattingRoomsResponse create(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		List<ChattingRoom> chattingRooms = memberEstimate.getExpertEstimateList()
			.stream()
			.map(x -> ChattingRoomRequest.toChattingRoom(memberEstimate))
			.toList();

		List<ChattingRoom> savedChattingRooms = chattingRoomRepository.saveAll(chattingRooms);

		chattingRooms.forEach(memberEstimate::addChattingRoom);

		return ChattingRoomsResponse.from(savedChattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAll() {
		List<ChattingRoom> chattingRooms = chattingRoomRepository.findAll();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllByMemberEstimateId(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		List<ChattingRoom> chattingRooms = chattingRoomRepository.findAllByMemberEstimate(memberEstimate);

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByMember(member);

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.flatMap(x -> chattingRoomRepository.findAllByMemberEstimate(x).stream())
			.toList();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomResponse findById(Long chattingRoomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

		return ChattingRoomResponse.from(chattingRoom);
	}

	@Transactional(readOnly = true)
	@CurrentExpertId
	public ChattingRoomsResponse findAllOfNormalByExpertId(Long expertId) {
		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByExpertId(expertId);

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.map(chattingRoomRepository::findAllByMemberEstimate)
			.flatMap(List::stream)
			.collect(Collectors.toList());

		return ChattingRoomsResponse.from(chattingRooms);
	}

	public void delete(Long chattingRoomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

		MemberEstimate memberEstimate = chattingRoom.getMemberEstimate();

		chattingRoomRepository.delete(chattingRoom);

		memberEstimate.removeChattingRoom(chattingRoom);
	}

	public <T> void sendMessage(WebSocketSession session, T message) {
		try{
			synchronized (session) {
				session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void handleWebSocketSession(WebSocketSession session, MessageRequest messageRequest) {
		if (messageRequest.chattingStatus().equals(ChattingStatus.ENTER)) {
			sessions.add(session);
		}

		sendMessagesToSessions(messageRequest);
	}

	private <T> void sendMessagesToSessions(T message) {
		sessions.parallelStream()
			.forEach(session -> sendMessage(session, message));
	}
}
