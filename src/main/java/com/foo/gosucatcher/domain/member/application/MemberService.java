package com.foo.gosucatcher.domain.member.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.infra.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public MemberSignUpResponse signUp(MemberSignUpRequest memberSignUpRequest) {
		Member member = MemberSignUpRequest.of(memberSignUpRequest);
		memberRepository.save(member);

		return new MemberSignUpResponse(true);
	}
}
