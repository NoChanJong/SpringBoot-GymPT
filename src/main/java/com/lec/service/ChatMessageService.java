package com.lec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lec.domain.ChatMessage;
import com.lec.domain.Member;
import com.lec.persistence.ChatMessageRepository;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public void markAsRead(ChatMessage message, Member receiver, Member sender) {
        // 업데이트 전에 로그 추가
        System.out.println("Before update - member1Read: " + message.isMember1Read());
        System.out.println("Before update - member2Read: " + message.isMember2Read());
        
        if (receiver == null) {
            // 추가: receiver가 null인 경우에는 업데이트하지 않음
            return;
        }
        
        if (sender.getId().equals(message.getRoomId().getMember1().getId()) && receiver.getId().equals(message.getRoomId().getMember2().getId())) {
            if (!message.isMember1Read()) {
                message.setMember1Read(true);
            }
        } else if (sender.getId().equals(message.getRoomId().getMember2().getId()) && receiver.getId().equals(message.getRoomId().getMember1().getId())) {
            if (!message.isMember2Read()) {
                message.setMember2Read(true);
            }
        } else {
            // 추가: 수신자가 채팅방에 들어온 경우에는 Member2Read를 true로 설정
            if (receiver.getId().equals(message.getRoomId().getMember2().getId())) {
                message.setMember2Read(true);
            }
            return;
        }
        
//        sender가 Member1이고 receiver가 Member2인 경우 member1Read를 업데이트하고, 
//        sender가 Member2이고 receiver가 Member1인 경우 member2Read를 업데이트 

        // 업데이트 후에 로그 추가
        System.out.println("After update - member1Read: " + message.isMember1Read());
        System.out.println("After update - member2Read: " + message.isMember2Read());

        chatMessageRepository.save(message);
    }




    // ChatMessage의 CRUD 작업을 위한 서비스 메서드들
    // ...

}