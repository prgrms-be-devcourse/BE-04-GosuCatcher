package com.foo.gosucatcher.view;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foo.gosucatcher.domain.item.application.MainItemService;
import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.response.main.MainItemsResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsSliceResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gosu-catcher")
public class HomeController {

	private final MainItemService mainItemService;
	private final SubItemService subItemService;

	@GetMapping
	public String home(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {

		MainItemsResponse mainItems = mainItemService.findAll();
		SubItemResponse item1 = subItemService.findById(1L);
		SubItemResponse item3 = subItemService.findById(3L);
		SubItemResponse item5 = subItemService.findById(5L);
		SubItemResponse item7 = subItemService.findById(7L);
		SubItemsSliceResponse cleaner = subItemService.findAllByMainItemName("청소", pageable);
		SubItemsSliceResponse partTimeJob = subItemService.findAllByMainItemName("알바", pageable);
		model.addAttribute("mainItems", mainItems);
		model.addAttribute("item1", item1);
		model.addAttribute("item3", item3);
		model.addAttribute("item5", item5);
		model.addAttribute("item7", item7);
		model.addAttribute("cleaner", cleaner);
		model.addAttribute("partTimeJob", partTimeJob);

		return "home";
	}
}
