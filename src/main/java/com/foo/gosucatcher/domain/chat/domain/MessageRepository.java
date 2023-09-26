package com.foo.gosucatcher.domain.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChattingRoom(ChattingRoom ChattingRoom);
}
