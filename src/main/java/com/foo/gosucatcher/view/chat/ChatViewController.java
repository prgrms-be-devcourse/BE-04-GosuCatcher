package com.foo.gosucatcher.view.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gosu-catcher")
public class ChatViewController {

	@GetMapping("/chats")
	public String chats() {
		return "chat/chats";
	}
}
