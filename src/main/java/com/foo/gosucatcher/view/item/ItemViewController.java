package com.foo.gosucatcher.view.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foo.gosucatcher.domain.item.application.MainItemService;
import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsSliceResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gosu-catcher")
public class ItemViewController {

	private final MainItemService mainItemService;
	private final SubItemService subItemService;

	@GetMapping("/items")
	public String items(@RequestParam("mainItemName") String mainItemName, Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
		SubItemsSliceResponse subItems = subItemService.findAllByMainItemName(mainItemName, pageable);

		model.addAttribute("subItems", subItems);
		model.addAttribute("mainItemName", mainItemName);

		return "items/items";
	}
}
