package com.foo.gosucatcher.view.estimate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gosu-catcher/hire")
@RequiredArgsConstructor
public class MemberEstimateViewController {

	private final SubItemService subItemService;
	private final ExpertService expertService;

	@GetMapping("/normal")
	public String createNormal(@RequestParam Long expertId, Model model) {
		model.addAttribute("expertId", expertId);

		SubItemsResponse subItemsResponse = expertService.getSubItemsByExpertId(expertId);
		model.addAttribute("subItems", subItemsResponse.subItemsResponse());

		return "estimates/normal-member-estimate";
	}

	@GetMapping("/auto/{subItemId}")
	public String createAuto(@PathVariable Long subItemId, Long memberId, Model model) {
		SubItemResponse subItemResponse = subItemService.findById(subItemId);
		String subItemName = subItemResponse.name();

		model.addAttribute("subItemId", subItemId);
		model.addAttribute("memberId", memberId);
		model.addAttribute("subItemName", subItemName);
		return "estimates/auto-member-estimate";
	}

	@GetMapping("/requests/send")
	public String estimates() {
		return "estimates/member-requested-estimates";
	}

	@GetMapping("/requests/send/details")
	public String estimatesDetails(@RequestParam Long id, @RequestParam String date) {
		return "estimates/member-requested-estimates-detail";
	}
}
