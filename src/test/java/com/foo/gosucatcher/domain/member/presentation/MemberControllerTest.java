package com.foo.gosucatcher.domain.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageUploadResponse;
import com.foo.gosucatcher.domain.member.application.MemberProfileService;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

@WebMvcTest(value = {MemberProfileController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class MemberControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MemberProfileService memberProfileService;

	@MockBean
	MemberRepository memberRepository;

	@MockBean
	ImageService imageService;
	//
	// private static final String baseUrl = "/api/v1/members";
	//
	// @Test
	// @DisplayName("이메일 중복 검사 성공")
	// public void duplicatedEmailCheckSuccessTest() throws Exception {
	// 	//given
	// 	MemberEmailDuplicateResponse memberEmailDuplicateResponse = new MemberEmailDuplicateResponse("test@test.com",
	// 		true);
	// 	given(memberService.checkDuplicatedEmail(anyString()))
	// 		.willReturn(memberEmailDuplicateResponse);
	//
	// 	//when, then
	// 	mockMvc.perform(get(baseUrl + "/signup?email={email}", "test@test.com"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.email").value("test@test.com"))
	// 		.andExpect(jsonPath("$.isUsable").value(true))
	// 		.andDo(print());
	// }
	//
	// @Test
	// @DisplayName("회원 가입 성공")
	// public void signupSuccessTest() throws Exception {
	// 	//given
	// 	MemberSignupRequest memberSignupRequest = new MemberSignupRequest("김뿅뿅", "test@test.com", "123123");
	// 	MemberSignupResponse memberSignupResponse = new MemberSignupResponse("test@test.com", "김뿅뿅");
	// 	given(memberService.signup(memberSignupRequest))
	// 		.willReturn(memberSignupResponse);
	//
	// 	//when, then
	// 	mockMvc.perform(post(baseUrl + "/signup")
	// 			.contentType(APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(memberSignupRequest)))
	// 		.andExpect(status().isCreated())
	// 		.andExpect(jsonPath("$.email").value("test@test.com"))
	// 		.andExpect(jsonPath("$.name").value("김뿅뿅"))
	// 		.andDo(print());
	// }
	//
	// @Test
	// @DisplayName("로그인 성공")
	// public void loginSuccessTest() throws Exception {
	// 	//given
	// 	MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "123123");
	// 	MemberCertifiedResponse memberCertifiedResponse = new MemberCertifiedResponse("mockAccessToken",
	// 		"mockRefreshToken");
	// 	given(memberService.login(memberLoginRequest))
	// 		.willReturn(memberCertifiedResponse);
	//
	// 	//when, then
	// 	mockMvc.perform(post(baseUrl + "/login")
	// 			.contentType(APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(memberLoginRequest)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
	// 		.andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"))
	// 		.andDo(print());
	// }
	//
	// @Test
	// @DisplayName("로그아웃 성공")
	// void logoutSuccessTest() throws Exception {
	// 	//given
	// 	String memberEmail = "test@test.com";
	// 	doNothing().when(memberService)
	// 		.logout(memberEmail);
	//
	// 	//when, then
	// 	mockMvc.perform(delete(baseUrl + "/logout"))
	// 		.andExpect(status().isNoContent())
	// 		.andDo(print());
	// }
	//
	// @Test
	// @DisplayName("회원탈퇴 성공")
	// void unregisterSuccessTest() {
	// 	//given
	//
	// 	//when, then
	//
	// }
	//
	// @Test
	// @DisplayName("회원정보 조회")
	// void findMemberProfileSuccessTest() throws Exception {
	// 	//given
	//
	// 	//when, then
	//
	// }
	//
	// @Test
	// @DisplayName("회원정보 변경")
	// void changeMemberProfileSuccessTest() {
	// 	//given
	//
	// 	//when, then
	//
	// }

	@Test
	@DisplayName("프로필 이미지 업로드 성공")
	void uploadImageSuccessTest() throws Exception {
		// given
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());

		ImageUploadResponse response = new ImageUploadResponse(List.of("test.jpg"));
		given(memberProfileService.uploadProfileImage(any(Long.class), any())).willReturn(response);

		// when -> then
		mockMvc.perform(multipart("/api/v1/members/profile/images")
				.file(multipartFile).param("memberId", "1"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.filenames[0]").value("test.jpg"))
			.andDo(print());
	}

	@Test
	@DisplayName("프로필 이미지 업로드 실패 - 비어있는 파일")
	void uploadImageFailureEmptyFileTest() throws Exception {
		// given
		MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

		given(memberProfileService.uploadProfileImage(any(Long.class), any())).willThrow(
			new InvalidValueException(ErrorCode.INVALID_IMAGE));

		// when -> then
		mockMvc.perform(multipart("/api/v1/members/profile/images")
				.file(emptyFile).param("memberId", "1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("F002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("지원하지 않는 이미지 파일 형식입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("프로필 이미지 업로드 실패 - Member 없음")
	void uploadImageFailureNoExpertTest() throws Exception {
		// given
		MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());

		given(memberProfileService.uploadProfileImage(any(Long.class), any())).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		// when -> then
		mockMvc.perform(multipart("/api/v1/members/profile/images")
				.file(validFile).param("memberId", "3"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("프로필 이미지 삭제 성공")
	void deleteImageSuccessTest() throws Exception {
		// given
		doNothing().when(memberProfileService).deleteProfileImage(anyLong());

		// when -> then
		mockMvc.perform(delete("/api/v1/members/profile/images").param("memberId", "1"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("프로필 이미지 가져오기 성공")
	void getImagesSuccessTest() throws Exception {
		// given
		List<String> filenames = List.of("test1.jpg");
		ImageResponse response = new ImageResponse(filenames);
		given(memberProfileService.getProfileImage(1L)).willReturn(response);

		// when -> then
		mockMvc.perform(get("/api/v1/members/profile/images").param("memberId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.filenames[0]").value("test1.jpg"))
			.andDo(print());
	}

	@Test
	@DisplayName("프로필 이미지 가져오기 실패: Member 없음")
	void getImagesFailureNotFoundTest() throws Exception {
		// given
		given(memberProfileService.getProfileImage(anyLong())).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		// when -> then
		mockMvc.perform(get("/api/v1/members/profile/images").param("memberId", "1"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
			.andDo(print());
	}
}
