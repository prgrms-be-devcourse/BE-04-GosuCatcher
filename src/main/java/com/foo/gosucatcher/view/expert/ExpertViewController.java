package com.foo.gosucatcher.view.expert;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gosu-catcher/experts")
public class ExpertViewController {

	@GetMapping
	public String home() {
		return "experts/experts";
	}

	@GetMapping("/profile")
	public String expertInfo(){
		return "experts/profile";
	}
}
