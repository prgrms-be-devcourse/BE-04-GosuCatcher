package com.foo.gosucatcher.domain.estimate.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.global.error.exception.BusinessException;

class ExpertEstimateTest {

	private Expert expert;
	private MemberRequestEstimate memberRequestEstimate;

	@BeforeEach
	void setUp() {
		expert = Expert.builder()
			.storeName("Store")
			.location("Location")
			.description("Description")
			.build();

		memberRequestEstimate = MemberRequestEstimate.builder()
			.location("Location")
			.preferredStartDate(LocalDateTime.now().plusDays(1))
			.detailedDescription("Description")
			.build();
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 10, 100, 1000})
	@DisplayName("고수 견적서 생성 성공")
	void createExpertResponseEstimateSuccessTest(int totalCost) {

		//when -> then
		assertDoesNotThrow(() -> ExpertEstimate.builder()
			.expert(expert)
			.memberRequestEstimate(memberRequestEstimate)
			.totalCost(totalCost)
			.description("Sample Description")
			.build());
	}

	@ParameterizedTest
	@ValueSource(ints = {-50, 0})
	@DisplayName("고수 견적서 생성 실패 - 총 금액이 0이거나, 음수이다")
	void createExpertResponseEstimateFailTest_InvalidTotalCost(int invalidTotalCost) {

		//when -> then
		assertThrows(BusinessException.class, () -> ExpertEstimate.builder()
			.expert(expert)
			.memberRequestEstimate(memberRequestEstimate)
			.totalCost(invalidTotalCost)
			.description("Sample Description")
			.build());
	}
}
