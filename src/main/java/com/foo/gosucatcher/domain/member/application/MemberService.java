package com.foo.gosucatcher.domain.member.application;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberRefreshRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailDuplicateResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
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
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public MemberPasswordFoundResponse findPassword(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberPasswordFoundResponse.from(member);
	}

	@Transactional(readOnly = true)
	public MemberEmailDuplicateResponse checkDuplicatedEmail(String email) {
		memberRepository.findByEmail(email)
			.ifPresent((member) -> {
				throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER);
			});

		return MemberEmailDuplicateResponse.from(email);
	}

	public MemberSignupResponse signup(MemberSignupRequest memberSignUpRequest) {
		Member signupMember = MemberSignupRequest.toMember(memberSignUpRequest);
		String signupEmail = signupMember.getEmail();

		checkDuplicatedEmail(signupEmail);

		signupMember.encodePassword(passwordEncoder);
		signupMember.updateMemberRole(Roles.ROLE_USER);
		Member savedMember = memberRepository.save(signupMember);

		memberProfileRepository.initializeMemberProfile(savedMember);

		return MemberSignupResponse.from(savedMember);
	}

	@Transactional
	public MemberCertifiedResponse login(MemberLoginRequest memberLoginRequest) {
		String loginRequestEmail = memberLoginRequest.email();
		Member member = memberRepository.findByEmail(loginRequestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member loginRequestMember = MemberLoginRequest.toMember(memberLoginRequest);
		member.authenticate(loginRequestMember, passwordEncoder);

		return getMemberCertifiedResponse(member);
	}

	public MemberCertifiedResponse refresh(MemberRefreshRequest memberRefreshRequest) {
		String refreshToken = memberRefreshRequest.refreshToken();

		if (jwtTokenProvider.isValidRefreshToken(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFICATION_FAIL);
		}

		Member member = getMemberByRefreshToken(refreshToken);

		String memberRefreshToken = member.getRefreshToken();
		if (!memberRefreshToken.equals(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFICATION_FAIL);
		}

		return getMemberCertifiedResponse(member);
	}

	public void logout(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.logout();
	}

	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		memberRepository.delete(member);
	}

	public MemberProfileResponse findMemberProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberProfileResponse.from(member);
	}

	public MemberProfileChangeResponse changeMemberProfile(Long memberId,
		@Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		//todo:휴대폰 인증 시스템 만들기
		Member changedMember = MemberProfileChangeRequest.toMember(memberProfileChangeRequest);
		member.updateProfile(changedMember, passwordEncoder);

		return MemberProfileChangeResponse.from(member);
	}

	public void uploadProfileImage(ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.updateProfileImage(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void deleteProfileImage(Long memberId) {
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

		member.refreshToken(refreshToken);

		return MemberCertifiedResponse.from(accessToken, refreshToken);
	}

	private Member getMemberByRefreshToken(String refreshToken) {
		Authentication authentication = jwtTokenProvider.getRefreshTokenAuthentication(refreshToken);
		String memberEmail = authentication.getName();

		return memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
	}
}
