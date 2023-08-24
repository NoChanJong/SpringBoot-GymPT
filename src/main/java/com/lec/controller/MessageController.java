package com.lec.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lec.domain.ChatMessage;
import com.lec.domain.ChatRoom;
import com.lec.domain.Member;
import com.lec.dto.ChatMessageDTO;
import com.lec.persistence.ChatMessageRepository;
import com.lec.persistence.ChatRoomRepository;
import com.lec.persistence.MemberRepository;
import com.lec.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;
    
    @MessageMapping("/chat/message")
    public void enter(ChatMessageDTO messageDTO) {
        if (messageDTO.getType() == ChatMessageDTO.MessageType.ENTER) {
            // 알림 메시지이므로 저장하지 않고 종료
            return;
        }

        // ChatRoom 엔티티 조회
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getRoomId()).orElse(null);
        if (chatRoom != null) {
            // ChatRoom의 recentMessage 필드 업데이트
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(chatRoom);

            Member sender = memberRepository.findById(messageDTO.getSender()).orElse(null);
            chatMessage.setSender(sender);

            Member receiver = memberRepository.findById(messageDTO.getReceiver()).orElse(null);
            chatMessage.setReceiver(receiver);

            chatMessage.setMessage(messageDTO.getMessage());

            // ChatRoom을 먼저 저장
            chatRoomRepository.save(chatRoom);

            // 채팅 메시지를 저장하고 저장된 메시지의 ID를 반환받음
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
            Integer messageId = savedMessage.getMessageId();

            // ChatRoom의 recentMessage 필드 업데이트
            chatRoom.setRecentMessage(savedMessage);
            chatRoomRepository.save(chatRoom);

            // 저장된 메시지를 생성한 사용자와 채팅방의 구독자들에게 전송
            sendingOperations.convertAndSend("/topic/chat/room/" + messageDTO.getRoomId(), messageDTO);

            // markAsRead 메서드 호출
            if (receiver != null) { // receiver가 null이 아닌 경우에만 업데이트 수행
            	chatMessageService.markAsRead(savedMessage, receiver, sender);
            }
        }
    }

    
    @GetMapping("/chat/messages")
    public List<ChatMessageDTO> loadMessages(@RequestParam("roomId") Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) {
            // 존재하지 않는 방 번호일 경우 예외 처리
            throw new IllegalArgumentException("Invalid roomId: " + roomId);
        }
        
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(chatRoom, Sort.by(Sort.Direction.DESC, "messageId"));
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageDTO messageDTO = new ChatMessageDTO();
            messageDTO.setType(ChatMessageDTO.MessageType.TALK);
            messageDTO.setMessageId(chatMessage.getMessageId());
            messageDTO.setRoomId(chatRoom.getRoom_id());
            messageDTO.setSender(chatMessage.getSender().getId());
            messageDTO.setReceiver(chatMessage.getReceiver().getId());
            messageDTO.setMessage(chatMessage.getMessage());
            messageDTO.setCreateDate_c(chatMessage.getCreateDate_c());
            messageDTOs.add(messageDTO);
            chatMessage.setMember1Read(true);
            chatMessage.setMember2Read(true);
        }
        chatMessageRepository.saveAll(chatMessages);
        return messageDTOs;
    }



}