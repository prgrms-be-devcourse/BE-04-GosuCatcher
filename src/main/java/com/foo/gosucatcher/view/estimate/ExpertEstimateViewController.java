package com.foo.gosucatcher.view.estimate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gosu-catcher")
public class ExpertEstimateViewController {

	@GetMapping("/auto-quote")
	public String createAuto() {

		return "estimates/auto-expert-estimate";
	}

	@GetMapping("/normal-response")
	public String createNormal(@RequestParam Long memberEstimateId, Model model) {
		model.addAttribute("memberEstimateId", memberEstimateId);

		return "estimates/normal-expert-estimate";
	}
}
