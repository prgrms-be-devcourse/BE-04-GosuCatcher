package com.foo.gosucatcher.domain.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;

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
		Member member = MemberSignUpRequest.to(memberSignUpRequest);
		memberRepository.save(member);

		return new MemberSignUpResponse(true);
	}

	public void logIn(MemberLogInRequest memberLogInRequest) {
		String email = memberLogInRequest.email();
		Member logInMember = memberRepository.findByEmail(email);

		String password = memberLogInRequest.password();
		logInMember.logIn(password);
	}

	public void deleteMember(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("찾는 멤버 없음"));
		member.deleteMember();
	}

	public String findPassword(String email) {
		Member member = memberRepository.findByEmail(email);

		return member.getPassword();
	}

	public void changeMemberInfo(long memberId, MemberInfoChangeRequest memberInfoChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("유저없음"));

		member.changeMemberInfo(memberInfoChangeRequest);
	}
}
