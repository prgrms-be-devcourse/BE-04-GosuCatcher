package com.foo.gosucatcher.view.estimate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gosu-catcher")
public class ExpertEstimateViewController {

	@GetMapping("/auto-quote")
	public String createAuto() {

		return "estimates/auto-expert-estimate";
	}
}
