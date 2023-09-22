package com.foo.gosucatcher.domain.review.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReplyRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReplyResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@AutoConfigureRestDocs
@WebMvcTest(value = {ReviewController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ReviewService reviewService;

	@Nested
	@DisplayName("<리뷰 등록>")
	class createTest {

		@DisplayName("성공 - 리뷰를 추가할 수 있다")
		@Test
		void create() throws Exception {
			// given
			long expertId = 0L;
			long subItemId = 0L;

			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("예시로 작성한 리뷰입니다", 5);
			ReviewResponse reviewResponse = new ReviewResponse(0L, "expert", "writer", "subItem",  5,"예시로 작성한 리뷰입니다", false,
				Map.of(
					"expert","김철수",
					"content","리뷰 남겨주셔서 감사합니다",
					"updatedAt","2023-09-22 17:14:09.490217"
				), List.of("https://gosu-catcher.s3.ap-northeast-2.amazonaws.com/test_image"), localDateTime);

			given(reviewService.create(anyLong(), anyLong(), anyLong(), any(ReviewCreateRequest.class), any(
				ImageUploadRequest.class)))
				.willReturn(reviewResponse);

			MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "file".getBytes());
			MockMultipartFile request = new MockMultipartFile("reviewCreateRequest", "reviewCreateRequest",
				"application/json",
				objectMapper.writeValueAsString(reviewCreateRequest).getBytes());
			mockMvc.perform(RestDocumentationRequestBuilders
					.multipart("/api/v1/reviews/{expertId}", expertId)
					.file(file)
					.file(request)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.param("memberId", "1")
					.param("subItemId", String.valueOf(subItemId))
					.with(csrf())
					.with(user("username").roles("USER"))
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.expert").value(reviewResponse.expert()))
				.andExpect(jsonPath("$.writer").value(reviewResponse.writer()))
				.andExpect(jsonPath("$.subItem").value(reviewResponse.subItem()))
				.andExpect(jsonPath("$.content").value(reviewCreateRequest.content()))
				.andExpect(jsonPath("$.rating").value(reviewCreateRequest.rating()))
				.andDo(document(
					"review-success",
					preprocessResponse(prettyPrint()),
					pathParameters(parameterWithName("expertId").description("전문가 ID")),
					responseFields(
						fieldWithPath(".id").type(JsonFieldType.NUMBER).description("생성된 리뷰의 아이디"),
						fieldWithPath(".expert").type(JsonFieldType.STRING).description("서비스 제공 전문가 "),
						fieldWithPath(".writer").type(JsonFieldType.STRING).description("리뷰 작성자"),
						fieldWithPath(".subItem").type(JsonFieldType.STRING).description("제공 서비스 이름"),
						fieldWithPath(".rating").type(JsonFieldType.NUMBER).description("별점"),
						fieldWithPath(".content").type(JsonFieldType.STRING).description("작성된 리뷰 본문"),
						fieldWithPath(".replyExisted").type(JsonFieldType.BOOLEAN).description("해당 댓글에 달린 답글의 여부"),
						fieldWithPath(".reply").type(JsonFieldType.OBJECT).description("답글"),
						fieldWithPath(".reply.content").type(JsonFieldType.STRING).description("리뷰에 대한 답글 본문"),
						fieldWithPath(".reply.expert").type(JsonFieldType.STRING).description("답글의 대상이 된 전문가"),
						fieldWithPath(".reply.updatedAt").type(JsonFieldType.STRING).description("답글 업데이트 일자"),
						fieldWithPath(".images").type(JsonFieldType.ARRAY).description("리뷰에 포함된 사진 url 목록"),
						fieldWithPath(".updatedAt").type(JsonFieldType.STRING).description("업데이트 일자")
					)));;
		}

		@DisplayName("실패 - 존재하지 않는 고수에 대해 리뷰를 등록할 수 없다")
		@Test
		void createFailed_NotFoundExpert() throws Exception {
			// given
			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("예시로 작성한 리뷰입니다", 5);

			MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "file".getBytes());
			MockMultipartFile request = new MockMultipartFile("reviewCreateRequest", "reviewCreateRequest",
				"application/json",
				objectMapper.writeValueAsString(reviewCreateRequest).getBytes());

			doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT))
				.when(reviewService).create(anyLong(), anyLong(), anyLong(), any(ReviewCreateRequest.class), any(
					ImageUploadRequest.class));

			long expertId = 0L;
			long subItemId = 0L;
			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders
					.multipart("/api/v1/reviews/{expertId}", expertId)
					.file(file)
					.file(request)
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.param("memberId", "1")
					.param("subItemId", String.valueOf(subItemId))
					.content(objectMapper.writeValueAsString(reviewCreateRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code")
					.value("E001"));
		}

		@DisplayName("실패 - 존재하지 않는 하위 서비스에 대해 리뷰를 등록할 수 없다")
		@Test
		void createFailed_NotFoundSubItem() throws Exception {
			// given
			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("예시로 작성한 리뷰입니다", 5);
			long expertId = 0L;
			long subItemId = 0L;

			MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "file".getBytes());
			MockMultipartFile request = new MockMultipartFile("reviewCreateRequest", "reviewCreateRequest",
				"application/json",
				objectMapper.writeValueAsString(reviewCreateRequest).getBytes());

			doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM))
				.when(reviewService).create(anyLong(), anyLong(), anyLong(), any(ReviewCreateRequest.class), any(
					ImageUploadRequest.class));

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders
					.multipart("/api/v1/reviews/{expertId}", expertId)
					.file(file)
					.file(request)
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.param("memberId", "1")
					.param("subItemId", String.valueOf(subItemId))
					.content(objectMapper.writeValueAsString(reviewCreateRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code")
					.value("SI001"));
		}
	}

	@Nested
	@DisplayName("<리뷰 검색/조회>")
	class FindTest {

		@DisplayName("성공 - 특정 고수에 대한 리뷰를 모두 조회할 수 있다")
		@Test
		void findAllByExpertId() throws Exception {
			// given
			ReviewCreateRequest firstReviewCreateRequest = new ReviewCreateRequest("예시로 작성한 첫번째 리뷰입니다", 5);
			ReviewCreateRequest secondReviewCreateRequest = new ReviewCreateRequest("예시로 작성한 두번째 리뷰입니다", 3);

			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewsResponse reviewsResponse = new ReviewsResponse(
				List.of(
					new ReviewResponse(0L, "expert", "writer", "subItem",  5,"예시로 작성한 리뷰입니다", false, new HashMap<>(), List.of(),
						localDateTime),
					new ReviewResponse(1L, "expert", "writer", "subItem",  5,"예시로 작성한 두번째 리뷰입니다", false, new HashMap<>(), List.of(),
						localDateTime)),
				true
			);

			long subItemId = 1L;
			long expertId = 1L;
			long writerId = 1L;

			given(reviewService.findAllByExpertIdAndSubItem(any(Long.class), any(Long.class), any(PageRequest.class)))
				.willReturn(reviewsResponse);

			// when
			// then
			mockMvc.perform(get("/api/v1/reviews/experts", expertId)
					.param("id", "1")
					.param("subItemId", String.valueOf(subItemId))
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].id").value(reviewsResponse.reviews().get(0).id()))
				.andExpect(jsonPath("$.reviews[0].expert").value(reviewsResponse.reviews().get(0).expert()))
				.andExpect(
					jsonPath("$.reviews[0].writer").value(reviewsResponse.reviews().get(0).writer()))
				.andExpect(jsonPath("$.reviews[0].rating").value(reviewsResponse.reviews().get(0).rating()))
				.andExpect(
					jsonPath("$.reviews[0].replyExisted").value(reviewsResponse.reviews().get(0).replyExisted()))
				.andExpect(jsonPath("$.reviews[0].reply").value(reviewsResponse.reviews().get(0).reply()))
				.andExpect(jsonPath("$.reviews[1].id").value(reviewsResponse.reviews().get(1).id()))
				.andExpect(jsonPath("$.reviews[1].expert").value(reviewsResponse.reviews().get(1).expert()))
				.andExpect(
					jsonPath("$.reviews[1].writer").value(reviewsResponse.reviews().get(1).writer()))
				.andExpect(jsonPath("$.reviews[1].rating").value(reviewsResponse.reviews().get(0).rating()))
				.andExpect(
					jsonPath("$.reviews[1].replyExisted").value(reviewsResponse.reviews().get(1).replyExisted()))
				.andExpect(jsonPath("$.reviews[1].reply").value(reviewsResponse.reviews().get(1).reply()));
		}

		@DisplayName("성공 - 특정 고수에 대한 리뷰의 개수를 조회할 수 있다")
		@Test
		void getCounts() throws Exception {
			// given
			long expertId = 1L;

			given(reviewService.countByExpertId(any(Long.class)))
				.willReturn(2L);

			// when
			// then
			mockMvc.perform(get("/api/v1/reviews/counts/experts")
					.with(csrf())
					.with(user("username").roles("USER"))
					.param("id", "1")
					.contentType(MediaType.APPLICATION_JSON))

				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(2L));
		}

		@DisplayName("성공 - 리뷰를 모두 조회할 수 있다")
		@Test
		void findAll() throws Exception {
			// given

			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewsResponse reviewsResponse = new ReviewsResponse(
				List.of(
					new ReviewResponse(0L, "expert", "writer", "subItem",  5,"예시로 작성한 리뷰입니다", false, new HashMap<>(), List.of(),
						localDateTime),
					new ReviewResponse(1L, "expert", "writer", "subItem",  5,"예시로 작성한 두번째 리뷰입니다", false, new HashMap<>(), List.of(),
						localDateTime)),
				true
			);

			given(reviewService.findAll(any(PageRequest.class)))
				.willReturn(reviewsResponse);


			// when
			// then
			mockMvc.perform(get("/api/v1/reviews/")
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].id").value(reviewsResponse.reviews().get(0).id()))
				.andExpect(jsonPath("$.reviews[0].expert").value(reviewsResponse.reviews().get(0).expert()))
				.andExpect(
					jsonPath("$.reviews[0].writer").value(reviewsResponse.reviews().get(0).writer()))
				.andExpect(jsonPath("$.reviews[0].rating").value(reviewsResponse.reviews().get(0).rating()))
				.andExpect(
					jsonPath("$.reviews[0].replyExisted").value(reviewsResponse.reviews().get(0).replyExisted()))
				.andExpect(jsonPath("$.reviews[0].reply").value(reviewsResponse.reviews().get(0).reply()))
				.andExpect(jsonPath("$.reviews[1].id").value(reviewsResponse.reviews().get(1).id()))
				.andExpect(jsonPath("$.reviews[1].expert").value(reviewsResponse.reviews().get(1).expert()))
				.andExpect(
					jsonPath("$.reviews[1].writer").value(reviewsResponse.reviews().get(1).writer()))
				.andExpect(jsonPath("$.reviews[1].rating").value(reviewsResponse.reviews().get(0).rating()))
				.andExpect(
					jsonPath("$.reviews[1].replyExisted").value(reviewsResponse.reviews().get(1).replyExisted()))
				.andExpect(jsonPath("$.reviews[1].reply").value(reviewsResponse.reviews().get(1).reply()));
		}


		@DisplayName("성공 - 리뷰를 아이디로 조회할 수 있다")
		@Test
		void findById() throws Exception {
			// given
			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewResponse reviewResponse = new ReviewResponse(0L, "expert", "writer", "subItem",  5,"예시로 작성한 리뷰입니다", false, new HashMap<>(), List.of(),
				localDateTime);

			long id = 1L;
			given(reviewService.findById(any(Long.class)))
				.willReturn(reviewResponse);

			// when
			// then
			mockMvc.perform(get("/api/v1/reviews/{id}", id)
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.expert").value(reviewResponse.expert()))
				.andExpect(jsonPath("$.writer").value(reviewResponse.writer()))
				.andExpect(jsonPath("$.subItem").value(reviewResponse.subItem()))
				.andExpect(jsonPath("$.content").value(reviewResponse.content()))
				.andExpect(jsonPath("$.rating").value(reviewResponse.rating()));
		}
	}

	@Nested
	@DisplayName("<리뷰 수정>")
	class UpdateTest {

		@DisplayName("성공 - 리뷰를 수정할 수 있다")
		@Test
		void update() throws Exception {
			// given
			long id = 0L;
			ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("수정하고자 하는 리뷰입니다", 3);

			// when
			// then
			when(reviewService.update(anyLong(), anyLong(), any(ReviewUpdateRequest.class)))
				.thenReturn(id);

			mockMvc.perform(patch("/api/v1/reviews/{id}", id)
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reviewUpdateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(id));
		}

		@DisplayName("실패 - 존재하지 않는(삭제된) 리뷰를 수정 요청 하는 경우 에러가 발생한다")
		@Test
		void updateFailed_deleted() throws Exception {
			// given
			long id = 0L;
			ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("수정하고자 하는 리뷰입니다", 3);
			doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW)).
				when(reviewService)
				.update(anyLong(), anyLong(), any(ReviewUpdateRequest.class));

			// when
			// then
			mockMvc.perform(patch("/api/v1/reviews/{id}", id)
					.with(csrf())
					.with(user("username").roles("USER"))
					.param("memberId", "1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reviewUpdateRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("R001"));
		}
	}

	@Nested
	@DisplayName("<리뷰 삭제>")
	class DeleteTest {

		@DisplayName("성공 - 리뷰를 삭제할 수 있다")
		@Test
		void delete() throws Exception {
			// given
			long id = 0L;
			doNothing()
				.when(reviewService)
				.delete(anyLong(), anyLong());

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/reviews/{id}", id)
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
		}
	}

	@Nested
	@DisplayName("<리뷰에 대한 답글>")
	class ReplyTest {

		@DisplayName("성공 - 리뷰에 대한 답글을 추가할 수 있다")
		@Test
		void create() throws Exception {
			// given
			long reviewId = 0L;

			LocalDateTime localDateTime = LocalDateTime.now();
			ReplyRequest replyRequest = new ReplyRequest("리뷰에 대한 답글입니다");
			ReplyResponse replyResponse = new ReplyResponse(0L, reviewId, "리뷰에 대한 답글입니다", localDateTime, localDateTime);

			given(reviewService.createReply(anyLong(), anyLong(), any(ReplyRequest.class)))
				.willReturn(replyResponse);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reviews/{reviewId}/replies", reviewId)
					.contentType(MediaType.APPLICATION_JSON)
					.param("memberId", "1")
					.with(csrf())
					.with(user("username").roles("USER"))
					.content(objectMapper.writeValueAsString(replyRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.reviewId").value(reviewId))
				.andExpect(jsonPath("$.content").value(replyRequest.content()));
		}

		@DisplayName("성공 - 리뷰에 대한 답글을 수정할 수 있다")
		@Test
		void update() throws Exception {
			// given
			long replyId = 0L;
			ReplyRequest replyRequest = new ReplyRequest("리뷰에 대한 답글입니다");

			given(reviewService.updateReply(anyLong(), any(ReplyRequest.class), anyLong()))
				.willReturn(replyId);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/reviews/replies")
					.param("id", "1")
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(replyRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(replyId));
		}

		@DisplayName("성공 - 리뷰에 대한 답글을 삭제할 수 있다")
		@Test
		void deleteReply() throws Exception {
			// given
			doNothing()
				.when(reviewService)
				.deleteReply(anyLong(), anyLong());

			// when
			// then
			mockMvc.perform(delete("/api/v1/reviews/replies")
					.param("id", "1")
					.with(csrf())
					.with(user("username").roles("USER"))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		}
	}
}
