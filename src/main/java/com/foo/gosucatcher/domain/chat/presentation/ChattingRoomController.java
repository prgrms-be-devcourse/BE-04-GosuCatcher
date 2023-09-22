package com.foo.gosucatcher.domain.chat.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import com.foo.gosucatcher.global.aop.CurrentExpertId;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

@Tag(name = "ChattingRoomController", description = "채팅방 조회/삭제 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chatting-rooms")
public class ChattingRoomController {

	private final ChattingRoomService chattingRoomService;

	@Operation(summary = "모든 채팅방 생성", description = "모든 채팅방을 생성합니다.", tags = {"ChattingRoomController"})
	@GetMapping
	public ResponseEntity<ChattingRoomsResponse> findAll() {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAll();

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "채팅방 ID로 채팅방 조회", description = "채팅방 ID로 채팅방을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/{chattingRoomId}")
	public ResponseEntity<ChattingRoomResponse> findById(
		@Parameter(description = "채팅방 ID", required = true)
		@PathVariable Long chattingRoomId) {
		ChattingRoomResponse chattingRoomResponse = chattingRoomService.findById(chattingRoomId);

		return ResponseEntity.ok(chattingRoomResponse);
	}

	@Operation(summary = "회원 견적서 ID로 채팅방 조회", description = "채팅방 ID로 채팅방을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/memberEstimates/{memberEstimateId}")
	public ResponseEntity<ChattingRoomsResponse> findAllByMemberEstimateId(
		@Parameter(description = "회원 견적서 ID", required = true)
		@PathVariable Long memberEstimateId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllByMemberEstimateId(memberEstimateId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "회원 ID로 채팅방 목록 조회", description = "회원 ID로 채팅방 목록을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/members")
	@CurrentMemberId
	public ResponseEntity<ChattingRoomsResponse> findAllByMemberId(
		@Parameter(description = "회원 ID", required = true)
		Long memberId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllByMemberId(memberId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "고수 ID로 채팅방 목록 조회", description = "고수 ID로 채팅방 목록을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/experts")
	@CurrentExpertId
	public ResponseEntity<ChattingRoomsResponse> findAllByExpertId(
		@Parameter(description = "고수 ID", required = true)
		Long expertId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllByExpertId(expertId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "고수 ID로 일반 견적 채팅방 목록 조회", description = "고수 ID로 일반 견적 채팅방 목록을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/normal")
	@CurrentExpertId
	public ResponseEntity<ChattingRoomsResponse> findAllOfNormalByExpertId(
		@Parameter(description = "고수 ID", required = true)
		Long expertId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllOfNormalByExpertId(expertId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "고수 ID로 바로 견적 채팅방 목록 조회", description = "고수 ID로 바로 견적 채팅방 목록을 조회합니다.", tags = {"ChattingRoomController"})
	@GetMapping("/auto")
	@CurrentExpertId
	public ResponseEntity<ChattingRoomsResponse> findAllOfAutoByExpertId(
		@Parameter(description = "고수 ID", required = true)
		Long expertId) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.findAllOfAutoByExpertId(expertId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}

	@Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.", tags = {"ChattingRoomController"})
	@DeleteMapping("/{chattingRoomId}")
	public ResponseEntity<Void> delete(
		@Parameter(description = "채팅방 ID", required = true)
		@PathVariable Long chattingRoomId) {
		chattingRoomService.delete(chattingRoomId);

		return ResponseEntity.ok(null);
	}
}
