package com.foo.gosucatcher.domain.chat.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chatting-rooms")
public class ChattingRoomController {

	private final ChattingRoomService chattingRoomService;

	@GetMapping
	public ResponseEntity<ChattingRoomsResponse> findAll() {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAll();

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@GetMapping("/{chattingRoomId}")
	public ResponseEntity<ChattingRoomResponse> findById(@PathVariable Long chattingRoomId) {
		ChattingRoomResponse chattingRoomResponse = chattingRoomService.findById(chattingRoomId);

		return ResponseEntity.ok(chattingRoomResponse);
	}

	@GetMapping("/memberEstimates/{memberEstimateId}")
	public ResponseEntity<ChattingRoomsResponse> findAllByMemberEstimateId(@PathVariable Long memberEstimateId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllByMemberEstimateId(memberEstimateId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@GetMapping("/members")
	@CurrentMemberId
	public ResponseEntity<ChattingRoomsResponse> findAllByMemberId(Long memberId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllByMemberId(memberId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@DeleteMapping("/{chattingRoomId}")
	public ResponseEntity<Void> delete(@PathVariable Long chattingRoomId) {
		chattingRoomService.delete(chattingRoomId);

		return ResponseEntity.ok(null);
	}
}
