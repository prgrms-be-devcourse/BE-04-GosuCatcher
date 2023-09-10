package com.foo.gosucatcher.domain.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, Long> {

    List<ChattingMessage> findAllByChattingRoom(ChattingRoom ChattingRoom);
}
