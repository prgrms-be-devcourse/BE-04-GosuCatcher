package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimateRepository;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpertResponseEstimateService {

	private final ExpertResponseEstimateRepository expertResponseRepository;
	private final MemberRequestEstimateRepository memberRequestEstimateRepository;
	private final ExpertRepository expertRepository;

	public ExpertResponseEstimateResponse create(Long expertId, ExpertResponseEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		MemberRequestEstimate memberRequestEstimate = memberRequestEstimateRepository.findById(
				request.memberRequestEstimateId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		ExpertResponseEstimate expertResponseEstimate = ExpertResponseEstimateCreateRequest.toExpertResponseEstimate(
			request, memberRequestEstimate, expert);

		expertResponseRepository.save(expertResponseEstimate);

		return ExpertResponseEstimateResponse.from(expertResponseEstimate);
	}

	@Transactional(readOnly = true)
	public ExpertResponseEstimatesResponse findAll() {
		List<ExpertResponseEstimate> expertResponseEstimateList = expertResponseRepository.findAll();

		return ExpertResponseEstimatesResponse.from(expertResponseEstimateList);
	}

	@Transactional(readOnly = true)
	public ExpertResponseEstimateResponse findById(Long id) {
		ExpertResponseEstimate expertResponseEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		return ExpertResponseEstimateResponse.from(expertResponseEstimate);
	}

	public Long update(Long id, ExpertResponseEstimateUpdateRequest request) {
		ExpertResponseEstimate foundExpertResponseEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		ExpertResponseEstimate expertResponseEstimate = ExpertResponseEstimateUpdateRequest.toExpertResponseEstimate(
			request, foundExpertResponseEstimate.getExpert(),
			foundExpertResponseEstimate.getMemberRequestEstimate());

		foundExpertResponseEstimate.update(expertResponseEstimate);

		return foundExpertResponseEstimate.getId();
	}

	public void delete(Long id) {
		ExpertResponseEstimate expertResponseEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		expertResponseRepository.delete(expertResponseEstimate);
	}
}
