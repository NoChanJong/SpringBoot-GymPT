package com.lec.dto;

import java.util.Date;

import com.lec.domain.ChatRoom;
import com.lec.domain.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDTO {

    private Long room_id; // UUID 값을 문자열로 변경
    private String roomName;
    private Member member1;
    private Member member2;
    private ChatMessageDTO recentMessage;
    private Date recentMessageCreateDate;

    public static ChatRoomDTO create(Long id, String name, Member member1, Member member2) {
        ChatRoomDTO room = new ChatRoomDTO();
//        room.room_id = id; // UUID 값을 문자열로 설정
        room.room_id = id;
        room.roomName = name;
        room.member1 = member1;
        room.member2 = member2;
        return room;
    }

    public static ChatRoom convertToEntity(ChatRoomDTO roomDTO) {
        ChatRoom room = new ChatRoom();
        room.setRoom_id(roomDTO.getRoom_id());
        room.setRoomName(roomDTO.getRoomName());
        room.setMember1(roomDTO.getMember1());
        room.setMember2(roomDTO.getMember2());
        return room;
    }

    public ChatRoom convertToEntity() {
        ChatRoom room = new ChatRoom();
        room.setRoom_id(this.room_id);
        room.setRoomName(this.roomName);
        room.setMember1(this.member1);
        room.setMember2(this.member2);
        return room;
    }
}
