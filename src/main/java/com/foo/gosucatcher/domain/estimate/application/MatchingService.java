package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.util.RandomElementSelector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchingService {

	private final SubItemRepository subItemRepository;
	private final ExpertEstimateRepository expertEstimateRepository;

	@Transactional(readOnly = true)
	public ExpertEstimatesResponse match(Long subItemId, String activityLocation) {
		SubItem subItem = subItemRepository.findById(subItemId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllBySubItemIdAndLocationAndIsAuto(
			subItem.getId(), activityLocation);

		List<ExpertEstimate> randomExpertEstimates = RandomElementSelector.selectRandomElements(expertEstimates, 10);

		return ExpertEstimatesResponse.from(randomExpertEstimates);
	}
}
