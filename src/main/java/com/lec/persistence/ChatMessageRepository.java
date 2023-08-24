package com.lec.persistence;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lec.domain.ChatMessage;
import com.lec.domain.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

	List<ChatMessage> findByRoomId(ChatRoom chatRoom, Sort by);
}

