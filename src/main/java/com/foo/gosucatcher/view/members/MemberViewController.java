package com.foo.gosucatcher.view.members;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foo.gosucatcher.domain.item.application.MainItemService;
import com.foo.gosucatcher.domain.item.application.SubItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gosu-catcher")
public class MemberViewController {

	private final MainItemService mainItemService;
	private final SubItemService subItemService;

	@GetMapping("/members/my-page")
	public String my_page() {
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
	public String estimate() {
		return "members/estimate";
	}

	@GetMapping("/recovery/password")
	public String recoveryPassword() {
		return "members/recoveryPassword";
	}
}
