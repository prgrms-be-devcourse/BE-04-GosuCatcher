package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;

public record MemberRequestEstimatesResponse (
	List<MemberRequestEstimate> memberRequestEstimates
) {
}