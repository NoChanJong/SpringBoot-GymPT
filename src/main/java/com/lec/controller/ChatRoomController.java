package com.lec.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lec.domain.ChatRoom;
import com.lec.dto.ChatRoomDTO;
import com.lec.persistence.ChatRoomRepository;
import com.lec.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/chat", "/member"})
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        Iterable<ChatRoom> chatRooms = chatRoomRepository.findAll();
        
        // 모델에 chatRooms를 설정하여 Thymeleaf 템플릿에 전달
        model.addAttribute("chatRooms", chatRooms);
        return "chat/room";
    }

	// 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDTO> room() {
        return chatRoomService.findAllRoom();
    }
    
    @PostMapping("room")
    @ResponseBody
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody ChatRoomDTO roomDTO, HttpServletResponse response) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoom_id(roomDTO.getRoom_id());
        chatRoom.setRoomName(roomDTO.getRoomName());
        chatRoom.setMember1(roomDTO.getMember1());
        chatRoom.setMember2(roomDTO.getMember2());

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        ChatRoomDTO createdRoomDTO = new ChatRoomDTO();
        createdRoomDTO.setRoom_id(savedRoom.getRoom_id());
        createdRoomDTO.setRoomName(savedRoom.getRoomName());
        createdRoomDTO.setMember1(savedRoom.getMember1());
        createdRoomDTO.setMember2(savedRoom.getMember2());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{room_id}")
                .buildAndExpand(createdRoomDTO.getRoom_id())
                .toUri();

        return ResponseEntity.created(location).body(createdRoomDTO);
    }


    // 채팅방 입장 화면
    @GetMapping("/room/enter/{room_id}")
    public String roomDetail(Model model, @PathVariable("room_id") Long roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }
    
    // 특정 채팅방 조회
    @GetMapping("/room/{room_id}")
    @ResponseBody
    public ChatRoomDTO roomInfo(@PathVariable("room_id") Long roomId) {
        return chatRoomService.findById(roomId);
    }
    
    // 채팅방 삭제
    @DeleteMapping("/room/{room_id}")
    @ResponseBody
    public ResponseEntity<String> deleteRoom(@PathVariable("room_id") Long roomId) {
        boolean isDeleted = chatRoomService.deleteRoom(roomId);
        if (isDeleted) {
            return ResponseEntity.ok("채팅방 삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 삭제 실패");
        }
    }
}
