package com.foo.gosucatcher.domain.review.presentation;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReplyRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReplyResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

	String apiBaseUrl = "/api/v1/reviews";

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

			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(0L, "예시로 작성한 리뷰입니다", 5);
			ReviewResponse reviewResponse = new ReviewResponse(0L, 0L, 0L, 0L, "예시로 작성한 리뷰입니다", 5, false,
				new Hashtable<>(), localDateTime, localDateTime);
			given(reviewService.create(any(Long.class), any(Long.class), any(ReviewCreateRequest.class)))
				.willReturn(reviewResponse);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/" + expertId)
					.contentType(MediaType.APPLICATION_JSON)
					.param("subItemId", String.valueOf(subItemId))
					.content(objectMapper.writeValueAsString(reviewCreateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.expertId").value(expertId))
				.andExpect(jsonPath("$.writerId").value(reviewCreateRequest.writerId()))
				.andExpect(jsonPath("$.subItemId").value(subItemId))
				.andExpect(jsonPath("$.content").value(reviewCreateRequest.content()))
				.andExpect(jsonPath("$.rating").value(reviewCreateRequest.rating()));
		}

		@DisplayName("실패 - 존재하지 않는 고수에 대해 리뷰를 등록할 수 없다")
		@Test
		void createFailed_NotFoundExpert() throws Exception {
			// given
			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(0L, "예시로 작성한 리뷰입니다", 5);

			doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT))
				.when(reviewService).create(any(Long.class), any(Long.class), any(ReviewCreateRequest.class));

			long subItemId = 0L;
			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/" + 0)
					.contentType(MediaType.APPLICATION_JSON)
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
			ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(0L, "예시로 작성한 리뷰입니다", 5);
			long expertId = 0L;
			long subItemId = 0L;

			doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM))
				.when(reviewService).create(any(Long.class), any(Long.class), any(ReviewCreateRequest.class));

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/" + expertId)
					.contentType(MediaType.APPLICATION_JSON)
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
			ReviewCreateRequest firstReviewCreateRequest = new ReviewCreateRequest(1L, "예시로 작성한 첫번째 리뷰입니다", 5);
			ReviewCreateRequest secondReviewCreateRequest = new ReviewCreateRequest(1L, "예시로 작성한 두번째 리뷰입니다", 3);

			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewsResponse reviewsResponse = new ReviewsResponse(
				List.of(
					new ReviewResponse(1L, 1L, 1L, 1L, "예시로 작성한 첫번째 리뷰입니다", 5, false, new Hashtable<>(), localDateTime,
						localDateTime),
					new ReviewResponse(2L, 1L, 1L, 1L, "예시로 작성한 두번째 리뷰입니다", 3, false, new Hashtable<>(), localDateTime,
						localDateTime)),
				true
			);

			long subItemId = 1L;
			long expertId = 1L;

			given(reviewService.findAllByExpertIdAndSubItem(any(PageRequest.class), any(Long.class), any(Long.class)))
				.willReturn(reviewsResponse);

			// when
			// then
			mockMvc.perform(get(apiBaseUrl + "/experts/" + expertId)
					.param("subItemId", String.valueOf(subItemId))
					.contentType(MediaType.APPLICATION_JSON))

				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].id").value(1L))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].expertId").value(expertId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[0].writerId").value(firstReviewCreateRequest.writerId()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].subItemId").value(subItemId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[0].content").value(
						firstReviewCreateRequest.content()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].rating").value(firstReviewCreateRequest.rating()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].id").value(2L))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].expertId").value(expertId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[1].writerId").value(secondReviewCreateRequest.writerId()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].subItemId").value(subItemId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[1].content").value(
						secondReviewCreateRequest.content()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].rating").value(secondReviewCreateRequest.rating()));
		}

		@DisplayName("성공 - 리뷰를 모두 조회할 수 있다")
		@Test
		void findAll() throws Exception {
			// given
			ReviewCreateRequest firstReviewCreateRequest = new ReviewCreateRequest(1L, "예시로 작성한 첫번째 리뷰입니다", 5);
			ReviewCreateRequest secondReviewCreateRequest = new ReviewCreateRequest(1L, "예시로 작성한 두번째 리뷰입니다", 3);

			LocalDateTime localDateTime = LocalDateTime.now();

			ReviewsResponse reviewsResponse = new ReviewsResponse(
				List.of(
					new ReviewResponse(1L, 1L, 1L, 1L, "예시로 작성한 첫번째 리뷰입니다", 5, false, new Hashtable<>(), localDateTime,
						localDateTime),
					new ReviewResponse(2L, 1L, 1L, 1L, "예시로 작성한 두번째 리뷰입니다", 3, false, new Hashtable<>(), localDateTime,
						localDateTime)),
				true
			);

			long expertId = 1L;
			long subItemId = 1L;
			given(reviewService.findAll(any(PageRequest.class)))
				.willReturn(reviewsResponse);

			// when
			// then
			mockMvc.perform(get(apiBaseUrl)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].id").value(1))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].expertId").value(expertId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[0].writerId").value(firstReviewCreateRequest.writerId()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].subItemId").value(subItemId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[0].content").value(
						firstReviewCreateRequest.content()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[0].rating").value(firstReviewCreateRequest.rating()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].id").value(2))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].expertId").value(expertId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[1].writerId").value(secondReviewCreateRequest.writerId()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].subItemId").value(subItemId))
				.andExpect(
					jsonPath("$.ReviewsSliceResponse[1].content").value(
						secondReviewCreateRequest.content()))
				.andExpect(jsonPath("$.ReviewsSliceResponse[1].rating").value(secondReviewCreateRequest.rating()));
		}

		@DisplayName("성공 - 리뷰를 아이디로 조회할 수 있다")
		@Test
		void findById() throws Exception {
			// given
			LocalDateTime localDateTime = LocalDateTime.now();
			ReviewResponse reviewResponse = new ReviewResponse(1L, 1L, 1L, 1L, "예시로 작성한 첫번째 리뷰입니다", 5, false,
				new Hashtable<>(), localDateTime,
				localDateTime);

			long id = 1L;
			given(reviewService.findById(any(Long.class)))
				.willReturn(reviewResponse);

			// when
			// then
			mockMvc.perform(get(apiBaseUrl + "/" + id)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.expertId").value(reviewResponse.expertId()))
				.andExpect(jsonPath("$.writerId").value(reviewResponse.writerId()))
				.andExpect(jsonPath("$.subItemId").value(reviewResponse.subItemId()))
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
			when(reviewService.update(id, reviewUpdateRequest))
				.thenReturn(id);

			mockMvc.perform(patch(apiBaseUrl + "/" + id)
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
				.update(any(Long.class), any(ReviewUpdateRequest.class));

			// when
			// then
			mockMvc.perform(patch(apiBaseUrl + "/" + id)
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
				.delete(id);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.delete(apiBaseUrl + "/" + id)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
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
			ReplyRequest replyRequest = new ReplyRequest(0L, "리뷰에 대한 답글입니다");
			ReplyResponse replyResponse = new ReplyResponse(0L, reviewId, "리뷰에 대한 답글입니다", localDateTime, localDateTime);

			given(reviewService.createReply(any(Long.class), any(ReplyRequest.class)))
				.willReturn(replyResponse);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/" + reviewId + "/replies")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(replyRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.reviewId").value(reviewId))
				.andExpect(jsonPath("$.content").value(replyRequest.content()));
		}

		@DisplayName("성공 - 리뷰에 대한 답글을 수정할 수 있다")
		@Test
		void update() throws Exception {
			// given
			long reviewId = 0L;
			long replyId = 0L;
			LocalDateTime localDateTime = LocalDateTime.now();
			ReplyRequest replyRequest = new ReplyRequest(0L, "리뷰에 대한 답글입니다");

			given(reviewService.updateReply(any(Long.class), any(Long.class), any(ReplyRequest.class)))
				.willReturn(replyId);

			// when
			// then
			mockMvc.perform(MockMvcRequestBuilders.patch(apiBaseUrl + "/" + reviewId + "/replies/" + replyId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(replyRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(replyId));
		}

		@DisplayName("성공 - 리뷰에 대한 답글을 아이디로 조회할 수 있다")
		@Test
		void findById() throws Exception {
			// given
			long reviewId = 0L;
			LocalDateTime localDateTime = LocalDateTime.now();
			ReplyResponse replyResponse = new ReplyResponse(0L, reviewId, "리뷰에 대한 답글입니다", localDateTime, localDateTime);

			long replyId = 1L;
			given(reviewService.findReplyById(any(Long.class), any(Long.class)))
				.willReturn(replyResponse);

			// when
			// then
			mockMvc.perform(get(apiBaseUrl + "/" + reviewId + "/replies/" + replyId)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.reviewId").value(reviewId))
				.andExpect(jsonPath("$.content").value(replyResponse.content()));
		}

		@DisplayName("성공 - 리뷰에 대한 답글을 삭제할 수 있다")
		@Test
		void deleteReply() throws Exception {
			// given
			long reviewId = 1L;
			long replyId = 4L;
			doNothing()
				.when(reviewService)
				.deleteReply(reviewId, replyId);

			// when
			// then
			mockMvc.perform(delete("/api/v1/reviews/1/replies/4")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		}

	}
}
