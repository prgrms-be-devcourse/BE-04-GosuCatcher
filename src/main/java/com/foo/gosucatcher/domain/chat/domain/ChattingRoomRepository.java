package com.foo.gosucatcher.domain.chat.domain;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    List<ChattingRoom> findAllByMemberEstimate(MemberEstimate memberEstimate);
}
