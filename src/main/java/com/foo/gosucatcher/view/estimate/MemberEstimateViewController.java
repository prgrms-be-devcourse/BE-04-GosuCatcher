package com.foo.gosucatcher.view.estimate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gosu-catcher/member-estimates")
public class MemberEstimateViewController {

	@GetMapping("/{memberId}/{expertId}/{subItemId}")
	public String create(@PathVariable Long memberId, @PathVariable Long expertId, @PathVariable Long subItemId, Model model) {
		model.addAttribute("subItemId", subItemId);
		model.addAttribute("memberId", memberId);
		model.addAttribute("expertId", expertId);

		return "estimate/member-estimate";
	}
}
