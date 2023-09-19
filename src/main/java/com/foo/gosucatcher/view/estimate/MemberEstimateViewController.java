package com.foo.gosucatcher.view.estimate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gosu-catcher/hire")
public class MemberEstimateViewController {

	@GetMapping("/normal/{subItemId}")
	public String createNormal(@PathVariable Long subItemId, @RequestParam Long memberId, @RequestParam Long expertId, Model model) {
		model.addAttribute("subItemId", subItemId);
		model.addAttribute("memberId", memberId);
		model.addAttribute("expertId", expertId);

		return "estimates/normal-member-estimate";
	}

	@GetMapping("/auto/{subItemId}")
	public String createAuto(@PathVariable Long subItemId, @RequestParam Long memberId, Model model) {
		model.addAttribute("subItemId", subItemId);
		model.addAttribute("memberId", memberId);

		return "estimates/auto-member-estimate";
	}
}
