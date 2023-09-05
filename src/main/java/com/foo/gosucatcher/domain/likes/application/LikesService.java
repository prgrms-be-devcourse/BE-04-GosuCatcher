package com.foo.gosucatcher.domain.likes.application;

import static com.foo.gosucatcher.global.error.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.likes.domain.Likes;
import com.foo.gosucatcher.domain.likes.domain.LikesRepository;
import com.foo.gosucatcher.domain.likes.dto.request.LikesRequest;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponse;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponses;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

	private final LikesRepository likesRepository;
	private final MemberRepository memberRepository;
	private final ExpertRepository expertRepository;

	@Transactional(readOnly = true)
	public LikesResponses findAll() {
		List<Likes> likes = likesRepository.findAll();

		if (likes.isEmpty()) {
			throw new EntityNotFoundException(NOT_FOUND_LIKES);
		}

		return LikesResponses.from(likes);
	}

	public void deleteById(Long id) {
		if (likesRepository.findById(id).isEmpty()) {
			throw new EntityNotFoundException(NOT_FOUND_LIKES);
		}

		likesRepository.deleteById(id);
	}

	public LikesResponse create(LikesRequest likesRequest) {
		Member member = memberRepository.findById(likesRequest.memberId())
				.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));
		Expert expert = expertRepository.findById(likesRequest.expertId())
				.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		Likes likes = LikesRequest.toLikes(member, expert);

		likesRepository.save(likes);

		return LikesResponse.from(likes);
	}

	@Transactional(readOnly = true)
	public Boolean checkStatus(LikesRequest likesRequest) {
		Member member = memberRepository.findById(likesRequest.memberId())
				.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));
		Expert expert = expertRepository.findById(likesRequest.expertId())
				.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		return likesRepository.findByMemberIdAndExpertId(member.getId(), expert.getId())
				.isPresent();
	}
}
