package com.foo.gosucatcher.domain.member.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.member.application.MemberService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailDuplicateResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;

@WebMvcTest(value = {MemberController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class MemberControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MemberService memberService;

	private static final String baseUrl = "/api/v1/members";

	@Test
	@DisplayName("이메일 중복 검사 성공")
	public void duplicatedEmailCheckSuccessTest() throws Exception {
		//given
		MemberEmailDuplicateResponse memberEmailDuplicateResponse = new MemberEmailDuplicateResponse("test@test.com",
			true);
		given(memberService.checkDuplicatedEmail(anyString()))
			.willReturn(memberEmailDuplicateResponse);

		//when, then
		mockMvc.perform(get(baseUrl + "/signup?email={email}", "test@test.com"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("test@test.com"))
			.andExpect(jsonPath("$.isUsable").value(true))
			.andDo(print());
	}

	@Test
	@DisplayName("회원 가입 성공")
	public void signupSuccessTest() throws Exception {
		//given
		MemberSignupRequest memberSignupRequest = new MemberSignupRequest("김뿅뿅", "test@test.com", "123123");
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse("test@test.com", "김뿅뿅");
		given(memberService.signup(memberSignupRequest))
			.willReturn(memberSignupResponse);

		//when, then
		mockMvc.perform(post(baseUrl + "/signup")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberSignupRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.email").value("test@test.com"))
			.andExpect(jsonPath("$.name").value("김뿅뿅"))
			.andDo(print());
	}

	@Test
	@DisplayName("로그인 성공")
	public void loginSuccessTest() throws Exception {
		//given
		MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "123123");
		MemberCertifiedResponse memberCertifiedResponse = new MemberCertifiedResponse("mockAccessToken",
			"mockRefreshToken");
		given(memberService.login(memberLoginRequest))
			.willReturn(memberCertifiedResponse);

		//when, then
		mockMvc.perform(post(baseUrl + "/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberLoginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
			.andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"))
			.andDo(print());
	}

	@Test
	@DisplayName("로그아웃 성공")
	void logoutSuccessTest() throws Exception {
		//given
		String memberEmail = "test@test.com";
		doNothing().when(memberService)
			.logout(memberEmail);

		//when, then
		mockMvc.perform(delete(baseUrl + "/logout"))
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	@Test
	@DisplayName("회원탈퇴 성공")
	void unregisterSuccessTest() {
		//given

		//when, then

	}

	@Test
	@DisplayName("회원정보 조회")
	void findMemberProfileSuccessTest() throws Exception {
		//given

		//when, then

	}

	@Test
	@DisplayName("회원정보 변경")
	void changeMemberProfileSuccessTest() {
		//given

		//when, then

	}
}
