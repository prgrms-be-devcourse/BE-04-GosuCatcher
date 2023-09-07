package com.foo.gosucatcher.domain.member.application;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberRefreshRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.domain.Roles;
import com.foo.gosucatcher.domain.member.exception.MemberCertifiedFailException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder passwordEncoder;

	public MemberSignUpResponse signup(@Validated MemberSignUpRequest memberSignUpRequest) {
		String signUpEmail = memberSignUpRequest.email();
		String signUpPassword = memberSignUpRequest.password();
		String signUpName = memberSignUpRequest.name();

		checkDuplicatedEmail(signUpEmail);

		String encodedPassword = passwordEncoder.encode(signUpPassword);
		Member signUpMember = Member.builder()
			.email(signUpEmail)
			.password(encodedPassword)
			.name(signUpName)
			.role(Roles.ROLE_USER)
			.build();
		Member savedMember = memberRepository.save(signUpMember);

		memberProfileRepository.initializeMemberProfile(savedMember);

		return MemberSignUpResponse.from(savedMember);
	}

	@Transactional
	public MemberCertifiedResponse login(@Validated MemberLoginRequest memberLoginRequest) {
		String loginRequestEmail = memberLoginRequest.email();
		Member foundMember = memberRepository.findByEmail(loginRequestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		String loginRequestPassword = memberLoginRequest.password();
		String memberPassword = foundMember.getPassword();
		if (!passwordEncoder.matches(loginRequestPassword, memberPassword)) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}

		return getMemberCertifiedResponse(foundMember);
	}

	public MemberCertifiedResponse refresh(MemberRefreshRequest memberRefreshRequest) {
		String refreshToken = memberRefreshRequest.refreshToken();

		if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFIED_FAIL);
		}

		Authentication authentication = jwtTokenProvider.getRefreshTokenAuthentication(refreshToken);
		String memberEmail = authentication.getName();
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String memberRefreshToken = member.getRefreshToken();
		if (!memberRefreshToken.equals(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFIED_FAIL);
		}

		return getMemberCertifiedResponse(member);
	}

	public void logout(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.logout();
	}

	@Transactional(readOnly = true)
	public MemberPasswordFoundResponse findPassword(@Validated @Email String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		return MemberPasswordFoundResponse.from(member);
	}

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(@Validated @Email String email) {
		memberRepository.findByEmail(email)
			.ifPresent((elem) -> {
				throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER_EMAIL);
			});
	}

	public long changeMemberInfo(@Validated @Min(0) long memberId,
		@Validated MemberInfoChangeRequest memberInfoChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member changedMember = MemberInfoChangeRequest.toMember(memberInfoChangeRequest);
		member.changeMemberInfo(changedMember);

		return memberId;
	}

	public void deleteMember(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		member.deleteMember();
	}

	public void uploadProfileImage(@Validated ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.changeProfileImageFile(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void removeProfileImage(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		ImageFile profileImageFile = member.getProfileImageFile();
		memberProfileRepository.deleteImage(profileImageFile);
	}

	private MemberCertifiedResponse getMemberCertifiedResponse(Member member) {
		String memberEmail = member.getEmail();
		Long memberId = member.getId();

		String accessToken = jwtTokenProvider.createAccessToken(memberEmail, memberId);
		String refreshToken = jwtTokenProvider.createRefreshToken(memberEmail, memberId);

		member.refresh(refreshToken);

		return MemberCertifiedResponse.from(accessToken, refreshToken);
	}
}
