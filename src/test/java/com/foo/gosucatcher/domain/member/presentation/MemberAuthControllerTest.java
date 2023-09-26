package com.foo.gosucatcher.domain.member.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.member.application.MemberAuthService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = MemberAuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class MemberAuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	MemberAuthService memberAuthService;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	@DisplayName("회원가입 성공")
	public void testSignup() throws Exception {
		// given
		MemberSignupRequest memberSignupRequest = new MemberSignupRequest("test-user", "test@test.com", "testtest");
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse("test@test.com", "test-user");

		given(memberAuthService.signup(any()))
			.willReturn(memberSignupResponse);

		//when -> then
		this.mockMvc.perform(post("/api/v1/members/signup")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberSignupRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("test-user"))
			.andExpect(jsonPath("$.email").value("test@test.com"))
			.andDo(print())
			.andDo(document("member-signup",
				Preprocessors.preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("name").type(STRING).description("사용자 이름(본명)"),
					fieldWithPath("email").type(STRING).description("이메일(로그인 아이디, 이메일 형식)"),
					fieldWithPath("password").type(STRING).description("비밀번호(5자 이상)")),
				responseFields(
					fieldWithPath("email").type(STRING).description("이메일(로그인 아이디, 이메일 형식)"),
					fieldWithPath("name").type(STRING).description("사용자 이름(본명)"))
			));
	}

	@Test
	@DisplayName("로그인 성공")
	public void testLogin() throws Exception {
		// given
		MemberLoginRequest memberLoginRequest = new MemberLoginRequest("test@test.com", "testtest");
		MemberCertifiedResponse memberCertifiedResponse = new MemberCertifiedResponse("MOCK_ACCESS_TOKEN",
			"MOCK_REFRESH_TOKEN");

		given(memberAuthService.login(any()))
			.willReturn(memberCertifiedResponse);

		// API 호출 및 응답 확인
		this.mockMvc.perform(post("/api/v1/members/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberLoginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("MOCK_ACCESS_TOKEN"))
			.andExpect(jsonPath("$.refreshToken").value("MOCK_REFRESH_TOKEN"))
			.andDo(print())
			.andDo(document("member-login",
				Preprocessors.preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(로그인 아이디, 이메일 형식)"),
					fieldWithPath("password").type(STRING).description("비밀번호(5자 이상)")),
				responseFields(
					fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
					fieldWithPath("refreshToken").type(STRING).description("리플래시 토큰"))
			));
	}

	// @Test
	// @DisplayName("토큰 재발급 성공")
	// public void testReissue() throws Exception {
	// 	// given
	// 	String headerValue = "Bearer MOCK_REFRESH_TOKEN";
	// 	JwtReissueResponse jwtReissueResponse = new JwtReissueResponse("NEW_MOCK_ACCESS_TOKEN");
	//
	// 	given(memberAuthService.reissue(any()))
	// 		.willReturn(jwtReissueResponse);
	//
	// 	this.mockMvc.perform(
	// 		get("/api/v1/members/reissue")
	// 			.with(csrf())
	// 			.with(user("username").roles("User"))
	// 			.header("Authorization", headerValue))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.accessToken").value("NEW_MOCK_ACCESS_TOKEN"))
	// 		.andDo(print())
	// 		.andDo(document("member-accessToken-reissue",
	// 			Preprocessors.preprocessRequest(prettyPrint()),
	// 			preprocessResponse(prettyPrint()),
	// 			responseFields(
	// 				fieldWithPath("accessToken").type(STRING).description("재 발급된 액세스 토큰"))
	// 		));
	// }
}
