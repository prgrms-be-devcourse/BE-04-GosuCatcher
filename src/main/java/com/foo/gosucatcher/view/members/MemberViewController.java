package com.foo.gosucatcher.view.members;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gosu-catcher")
public class MemberViewController {

	@GetMapping("/members/my-page")
	public String myPage() {
		return "members/my-page";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "members/joinForm";
	}
}
