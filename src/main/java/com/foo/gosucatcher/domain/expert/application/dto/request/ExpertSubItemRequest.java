package com.foo.gosucatcher.domain.expert.application.dto.request;

import javax.validation.constraints.NotBlank;

public record ExpertSubItemRequest(
        @NotBlank(message = "서비스 이름을 입력해주세요.")
        String subItemName
) {
}
