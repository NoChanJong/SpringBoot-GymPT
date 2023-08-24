package com.lec.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    
    private Integer messageId;
    private Long roomId;
    private String sender;
    private String receiver;
    private String message;
    private Date createDate_c;
    private boolean member1Read; // Add the is_member1_read field
    private boolean member2Read; // Add the is_member1_read field

    // Constructors, getters, setters, and other methods can be added as needed
}
