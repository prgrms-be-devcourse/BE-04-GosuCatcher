package com.foo.gosucatcher.view.members;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@GetMapping("/login")
	public String login() {
		return "members/login";
	}

	@GetMapping("/requests/send")
	public String estimates() {
		return "members/estimates";
	}

	@GetMapping("/requests/send/details")
	public String estimatesDetails(@RequestParam Long id) {
		return "members/estimates-detail";
	}

	@GetMapping("/recovery/password")
	public String recoveryPassword() {
		return "members/recoveryPassword";
	}
}
