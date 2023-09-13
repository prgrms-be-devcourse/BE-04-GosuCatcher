package com.foo.gosucatcher.domain.expert.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.infrastructure.FileSystemExpertImageService;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

@WebMvcTest(value = {ExpertController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ExpertControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ExpertService expertService;

	@MockBean
	MemberRepository memberRepository;

	@MockBean
	ExpertRepository expertRepository;

	@MockBean
	FileSystemExpertImageService imageService;

	@Mock
	private Member member;

	private ExpertCreateRequest expertCreateRequest;

	@BeforeEach
	void setUp() {
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));

		expertCreateRequest = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

	}

	@Test
	@DisplayName("고수 등록 성공")
	void createExpertSuccessTest() throws Exception {
		// given
		ExpertResponse expertResponse = new ExpertResponse(1L, "업체명1", "위치1", 100, "부가설명1");
		given(expertService.create(any(ExpertCreateRequest.class), eq(1L))).willReturn(expertResponse);

		// when -> then
		mockMvc.perform(
				post("/api/v1/experts").contentType(MediaType.APPLICATION_JSON).param("memberId", "1")
					.content(objectMapper.writeValueAsString(expertCreateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("업체명1"))
			.andExpect(jsonPath("$.location").value("위치1"))
			.andExpect(jsonPath("$.maxTravelDistance").value(100))
			.andExpect(jsonPath("$.description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 등록 실패: 존재하지 않는 회원 ID")
	void createExpertFailTest_notFoundMember() throws Exception {
		// given
		given(expertService.create(any(ExpertCreateRequest.class), eq(9999L)))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		ExpertCreateRequest request = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

		// when -> then
		mockMvc.perform(post("/api/v1/experts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.param("memberId", "9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 등록 실패: 중복된 상점명")
	void createExpertFailTest_duplication() throws Exception {
		// given
		ExpertCreateRequest duplicatedExpertCreateRequest = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

		given(expertService.create(any(ExpertCreateRequest.class), eq(1L)))
			.willThrow(new EntityNotFoundException(ErrorCode.DUPLICATED_EXPERT_STORENAME));

		// when -> then
		mockMvc.perform(post("/api/v1/experts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicatedExpertCreateRequest))
				.param("memberId", "1"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("상점명이 중복될 수 없습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 ID로 조회 성공")
	void getExpertByIdSuccessTest() throws Exception {
		// given
		ExpertResponse expertResponse = new ExpertResponse(1L, "업체명1", "위치1", 100, "부가설명1");
		given(expertService.findById(1L)).willReturn(expertResponse);

		// when -> then
		mockMvc.perform(get("/api/v1/experts/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("업체명1"))
			.andExpect(jsonPath("$.location").value("위치1"))
			.andExpect(jsonPath("$.maxTravelDistance").value(100))
			.andExpect(jsonPath("$.description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 ID로 조회 실패: 존재하지 않는 고수 ID")
	void getExpertByIdFailTest_notFoundExpert() throws Exception {
		// given
		given(expertService.findById(eq(9999L))).willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		// when -> then
		mockMvc.perform(get("/api/v1/experts/9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 전체 조회 성공")
	void getAllExpertsSuccessTest() throws Exception {
		// given
		List<Expert> expertList = List.of(
			Expert.builder()
				.member(member)
				.storeName("업체명1")
				.location("위치1")
				.maxTravelDistance(100)
				.description("부가설명1")
				.build()
		);

		ExpertsResponse expertsResponse = ExpertsResponse.from(expertList);
		given(expertService.findAll()).willReturn(expertsResponse);

		// when -> then
		mockMvc.perform(get("/api/v1/experts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertsResponse[0].storeName").value("업체명1"))
			.andExpect(jsonPath("$.expertsResponse[0].location").value("위치1"))
			.andExpect(jsonPath("$.expertsResponse[0].maxTravelDistance").value(100))
			.andExpect(jsonPath("$.expertsResponse[0].description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 수정 성공")
	void updateExpertSuccessTest() throws Exception {
		// given
		ExpertUpdateRequest updateRequest = new ExpertUpdateRequest("새로운 업체명", "새로운 위치", 150, "새로운 부가설명");
		ExpertResponse expertResponse = new ExpertResponse(1L, "새로운 업체명", "새로운 위치", 150, "새로운 부가설명");

		given(expertService.update(1L, updateRequest)).willReturn(1L);

		// when -> then
		mockMvc.perform(patch("/api/v1/experts/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 수정 실패: 존재하지 않는 고수 ID")
	void updateExpertFailTest_notFoundExpert() throws Exception {
		// given
		ExpertUpdateRequest updateRequest = new ExpertUpdateRequest("새로운 업체명", "새로운 위치", 150, "새로운 부가설명");
		given(expertService.update(eq(9999L), any(ExpertUpdateRequest.class)))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		// when -> then
		mockMvc.perform(patch("/api/v1/experts/9999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 삭제 성공")
	void deleteExpertSuccessTest() throws Exception {
		// given
		doNothing().when(expertService).delete(1L);

		// when -> then
		mockMvc.perform(delete("/api/v1/experts/1")).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@DisplayName("고수 삭제 실패: 존재하지 않는 고수 ID")
	void deleteExpertFailTest_notFoundExpert() throws Exception {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT)).when(expertService).delete(eq(9999L));

		// when -> then
		mockMvc.perform(delete("/api/v1/experts/9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 업로드 성공")
	void uploadImageSuccessTest() throws Exception {
		// given
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());

		given(imageService.store(any(Long.class), any())).willReturn("test.jpg");

		// when -> then
		mockMvc.perform(multipart("/api/v1/experts/1/images")
				.file(multipartFile))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.filename").value("test.jpg"))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 업로드 실패 - 비어있는 파일")
	void uploadImageFailureEmptyFileTest() throws Exception {
		// given
		MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

		given(imageService.store(any(Long.class), any())).willThrow(new InvalidValueException(ErrorCode.INVALID_IMAGE));

		// when -> then
		mockMvc.perform(multipart("/api/v1/experts/1/images")
				.file(emptyFile))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("F002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("지원하지 않는 이미지 파일 형식입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 업로드 실패 - Expert 없음")
	void uploadImageFailureNoExpertTest() throws Exception {
		// given
		MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());

		given(imageService.store(any(Long.class), any())).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		// when -> then
		mockMvc.perform(multipart("/api/v1/experts/1/images")
				.file(validFile))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 가져오기 성공")
	void getImageSuccessTest() throws Exception {
		// given
		Resource resource = new ByteArrayResource("test image content".getBytes());

		given(imageService.loadAsResource(1L, "test.jpg")).willReturn(resource);

		// when -> then
		mockMvc.perform(get("/api/v1/experts/1/images/test.jpg"))
			.andExpect(status().isOk())
			.andExpect(content().bytes("test image content".getBytes()))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 가져오기 실패: 이미지 없음")
	void getImageFailureNotFoundTest() throws Exception {
		// given
		given(imageService.loadAsResource(anyLong(), anyString())).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE));

		// when -> then
		mockMvc.perform(get("/api/v1/experts/1/images/test.jpg"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("F001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 이미지 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 삭제 성공")
	void deleteImageSuccessTest() throws Exception {
		// given
		doNothing().when(imageService).delete(1L, "test.jpg");

		// when -> then
		mockMvc.perform(delete("/api/v1/experts/1/images/test.jpg"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("이미지 삭제 실패: 이미지 없음")
	void deleteImageFailureNotFoundTest() throws Exception {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE)).when(imageService)
			.delete(anyLong(), anyString());

		// when -> then
		mockMvc.perform(delete("/api/v1/experts/1/images/test.jpg"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("F001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 이미지 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("모든 이미지 가져오기 성공")
	void getImagesSuccessTest() throws Exception {
		// given
		ImageResponse imageResponse1 = new ImageResponse("test1.jpg",
			"http://localhost/api/v1/experts/1/images/test1.jpg", 0L);
		ImageResponse imageResponse2 = new ImageResponse("test2.jpg",
			"http://localhost/api/v1/experts/1/images/test2.jpg", 0L);
		List<ImageResponse> imageResponses = List.of(imageResponse1, imageResponse2);

		given(imageService.loadAll(1L)).willReturn(imageResponses);

		// when -> then
		mockMvc.perform(get("/api/v1/experts/1/images"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].filename").value("test1.jpg"))
			.andExpect(jsonPath("$[0].url").value("http://localhost/api/v1/experts/1/images/test1.jpg"))
			.andExpect(jsonPath("$[0].size").value(0))
			.andExpect(jsonPath("$[1].filename").value("test2.jpg"))
			.andExpect(jsonPath("$[1].url").value("http://localhost/api/v1/experts/1/images/test2.jpg"))
			.andExpect(jsonPath("$[1].size").value(0))
			.andDo(print());
	}

	@Test
	@DisplayName("모든 이미지 가져오기 실패: 전문가 아이디 없음")
	void getImagesFailureNotFoundTest() throws Exception {
		// given
		given(imageService.loadAll(anyLong())).willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		// when -> then
		mockMvc.perform(get("/api/v1/experts/1/images"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

}
