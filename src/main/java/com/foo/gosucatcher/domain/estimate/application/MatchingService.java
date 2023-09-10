package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
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

	private final ExpertEstimateRepository expertEstimateRepository;

	@Transactional(readOnly = true)
	public ExpertAutoEstimatesResponse match(Long subItemId, String activityLocation) {
		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllBySubItemIdAndLocationAndIsAuto(
				subItemId, activityLocation);

		List<ExpertEstimate> randomExpertEstimates = RandomElementSelector.selectRandomElements(expertEstimates, 10);

		return ExpertAutoEstimatesResponse.from(randomExpertEstimates);
	}
}
