package com.foo.gosucatcher.domain.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;

	public MemberService(MemberRepository memberRepository, MemberProfileRepository memberProfileRepository) {
		this.memberRepository = memberRepository;
		this.memberProfileRepository = memberProfileRepository;
	}

	public void uploadProfileImage(ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.changeProfileImageFile(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void removeProfileImage(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		memberProfileRepository.deleteImage(member);
	}

	//////////////////

	public MemberSignUpResponse signUp(MemberSignUpRequest memberSignUpRequest) {
		Member member = MemberSignUpRequest.to(memberSignUpRequest);
		memberRepository.save(member);

		return new MemberSignUpResponse(true);
	}

	@Transactional(readOnly = true)
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

	@Transactional(readOnly = true)
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
