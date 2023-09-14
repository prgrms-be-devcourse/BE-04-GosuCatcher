package com.foo.gosucatcher.domain.chat.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChattingRoomController {

	private final ChattingRoomService chattingRoomService;

	@PostMapping("/{memberEstimateId}")
	public ChattingRoomsResponse create(@PathVariable Long memberEstimateId) {
		return chattingRoomService.create(memberEstimateId);
	}

	@GetMapping
	public ChattingRoomsResponse findAll() {
		return chattingRoomService.findAll();
	}
}
