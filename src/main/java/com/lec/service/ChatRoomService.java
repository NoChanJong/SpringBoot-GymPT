package com.lec.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lec.domain.ChatMessage;
import com.lec.domain.ChatRoom;
import com.lec.domain.Member;
import com.lec.dto.ChatMessageDTO;
import com.lec.dto.ChatRoomDTO;
import com.lec.persistence.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    
//    public List<ChatRoomDTO> findAllRoom() {
//        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
//        chatRooms.sort(Comparator.comparing(ChatRoom::getRoom_id).reversed()); // room_id를 기준으로 내림차순 정렬
//        return chatRooms.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//    public List<ChatRoomDTO> findAllRoom() {
//        List<Object[]> roomsAndCreateDates = chatRoomRepository.findRoomsAndRecentMessageCreateDates();
//        List<ChatRoomDTO> roomDTOs = new ArrayList<>();
//
//        for (Object[] roomAndCreateDate : roomsAndCreateDates) {
//            ChatRoom room = (ChatRoom) roomAndCreateDate[0];
//            Date createDate = (Date) roomAndCreateDate[1];
//
//            ChatRoomDTO roomDTO = ChatRoomDTO.create(
//                room.getRoom_id(),
//                room.getRoomName(),
//                room.getMember1(),
//                room.getMember2()
//            );
//
//            if (!room.getMessages().isEmpty()) {
//                ChatMessage recentMessage = room.getMessages().get(room.getMessages().size() - 1);
//                ChatMessageDTO recentMessageDTO = new ChatMessageDTO();
//                recentMessageDTO.setMessageId(recentMessage.getMessageId());
//                recentMessageDTO.setRoomId(room.getRoom_id());
//                recentMessageDTO.setSender(recentMessage.getSender().getId());
//                recentMessageDTO.setMessage(recentMessage.getMessage());
//                recentMessageDTO.setCreateDate_c(recentMessage.getCreateDate_c());
//                recentMessageDTO.setMember1Read(recentMessage.isMember1Read());
//                recentMessageDTO.setMember2Read(recentMessage.isMember2Read());
//
//                roomDTO.setRecentMessage(recentMessageDTO);
//            }
//
//            roomDTO.setRecentMessageCreateDate(createDate);
//
//            roomDTOs.add(roomDTO);
//        }
//
//        return roomDTOs;
//    }
    public List<ChatRoomDTO> findAllRoom() {
        List<Object[]> roomsAndCreateDates = chatRoomRepository.findRoomsAndRecentMessageCreateDates();
        List<ChatRoomDTO> roomDTOs = new ArrayList<>();

        for (Object[] roomAndCreateDate : roomsAndCreateDates) {
            ChatRoom room = (ChatRoom) roomAndCreateDate[0];
            Date createDate = (Date) roomAndCreateDate[1];

            ChatRoomDTO roomDTO = ChatRoomDTO.create(
                room.getRoom_id(),
                room.getRoomName(),
                room.getMember1(),
                room.getMember2()
            );

            if (!room.getMessages().isEmpty()) {
                ChatMessage recentMessage = room.getMessages().get(room.getMessages().size() - 1);
                ChatMessageDTO recentMessageDTO = new ChatMessageDTO();
                recentMessageDTO.setMessageId(recentMessage.getMessageId());
                recentMessageDTO.setRoomId(room.getRoom_id());
                recentMessageDTO.setSender(recentMessage.getSender().getId());
                recentMessageDTO.setReceiver(recentMessage.getReceiver().getId());
                recentMessageDTO.setMessage(recentMessage.getMessage());
                recentMessageDTO.setCreateDate_c(recentMessage.getCreateDate_c());
                recentMessageDTO.setMember1Read(recentMessage.isMember1Read());
                recentMessageDTO.setMember2Read(recentMessage.isMember2Read());

                roomDTO.setRecentMessage(recentMessageDTO);
            }

            roomDTO.setRecentMessageCreateDate(createDate);

            roomDTOs.add(roomDTO);
        }

        return roomDTOs;
    }

    public void markAsRead(ChatMessage message, Member receiver, Member sender) {
        chatMessageService.markAsRead(message, receiver, sender); // ChatMessageService의 markAsRead 호출
    }

    public ChatRoomDTO findById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
        return convertToDto(chatRoom);
    }
    
    public ChatRoomDTO createRoom(ChatRoomDTO chatRoomDto) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoom_id(chatRoomDto.getRoom_id()); 
        chatRoom.setRoomName(chatRoomDto.getRoomName());
        chatRoom.setMember1(chatRoomDto.getMember1());
        chatRoom.setMember2(chatRoomDto.getMember2());
        
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return convertToDto(savedRoom);
    }
//    public ChatRoomDTO createRoom(ChatRoomDTO chatRoomDto) {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setRoom_id(UUID.randomUUID().toString()); // UUID 값을 문자열로 설정
//        chatRoom.setRoomName(chatRoomDto.getRoomName());
//        chatRoom.setMember1(chatRoomDto.getMember1());
//        chatRoom.setMember2(chatRoomDto.getMember2());
//        
//        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
//        return convertToDto(savedRoom);
//    }
    
    public boolean deleteRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
        return true; 
    }
    
    private ChatRoomDTO convertToDto(ChatRoom chatRoom) {
        ChatRoomDTO chatRoomDto = new ChatRoomDTO();
        chatRoomDto.setRoom_id(chatRoom.getRoom_id());
        chatRoomDto.setRoomName(chatRoom.getRoomName());
        chatRoomDto.setMember1(chatRoom.getMember1()); // member1_id 필드 설정
        chatRoomDto.setMember2(chatRoom.getMember2()); // member2_id 필드 설정
        // 추가적인 필드들을 가져와 ChatRoomDTO에 설정
        
        return chatRoomDto;
    }

}
