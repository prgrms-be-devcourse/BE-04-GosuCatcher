package com.foo.gosucatcher.domain.chat.presentation;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.chat.application.ChattingService;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

	private final ChattingService chattingService;

	@PostMapping
	public ChattingRoom createRoom() {
		return chattingService.createRoom();
	}

	@GetMapping
	public List<ChattingRoom> findAllRoom() {
		return chattingService.findAllRoom();
	}
}
