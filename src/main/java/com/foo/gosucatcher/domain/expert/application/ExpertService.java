package com.foo.gosucatcher.domain.expert.application;

import static com.foo.gosucatcher.global.error.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpertService {

	private final ExpertRepository expertRepository;
	private final MemberRepository memberRepository;

	public ExpertResponse create(ExpertCreateRequest request, long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

		Expert newExpert = ExpertCreateRequest.toExpert(member, request);
		expertRepository.save(newExpert);

		return ExpertResponse.from(newExpert);
	}

	@Transactional(readOnly = true)
	public ExpertResponse findById(Long id) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		return ExpertResponse.from(expert);
	}

	@Transactional(readOnly = true)
	public List<Expert> findAll() {
		return expertRepository.findAll();
	}

	public Long update(Long id, ExpertUpdateRequest request) {
		Expert existingExpert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		Expert updatedExpert = ExpertUpdateRequest.toExpert(request);
		existingExpert.update(updatedExpert);

		return existingExpert.getId();
	}

	public void delete(Long id) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		expertRepository.delete(expert);
	}
}
