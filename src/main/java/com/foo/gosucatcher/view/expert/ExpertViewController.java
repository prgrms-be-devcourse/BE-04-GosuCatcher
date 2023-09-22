package com.foo.gosucatcher.view.expert;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gosu-catcher/experts")
@RequiredArgsConstructor
public class ExpertViewController {

	private final ReviewService reviewService;
	private final ExpertService expertService;

	@GetMapping
	public String myPage() {

		return "experts/experts";
	}

	@GetMapping("/search")
	public String search() {

		return "experts/experts-search";
	}

	@GetMapping("/profile/{expertId}")
	public String expertInfo(@PathVariable Long expertId, Model model,
		@PageableDefault(sort = "updatedAt", size = 100, direction = Sort.Direction.DESC) Pageable pageable){

		ReviewsResponse response = reviewService.findAllByExpertIdAndSubItem(expertId, null, pageable);
		ExpertResponse expertResponse = expertService.findById(expertId);
		SubItemsResponse subItems = expertService.getSubItemsByExpertId(expertId);

		model.addAttribute("reviews", response);
		model.addAttribute("profile", expertResponse);
		model.addAttribute("subItems", subItems);



		return "experts/profile";
	}
}
