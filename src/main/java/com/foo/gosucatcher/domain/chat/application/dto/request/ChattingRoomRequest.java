package com.foo.gosucatcher.domain.chat.application.dto.request;

import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;

public record ChattingRoomRequest(
        Long memberEstimateId
) {

    public static ChattingRoom toChattingRoom(MemberEstimate memberEstimate) {

        return ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();
    }
}
