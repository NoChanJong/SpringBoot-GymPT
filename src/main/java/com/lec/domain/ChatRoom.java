package com.lec.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "chat_room")
public class ChatRoom {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long room_id;
    
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "member1_id", referencedColumnName = "id")
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2_id", referencedColumnName = "id")
    private Member member2;

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<ChatMessage> messages;
    
    @ManyToOne
    @JoinColumn(name = "recent_message_id", referencedColumnName = "messageId")
    private ChatMessage recentMessage;
    // Constructors, getters, setters, and other methods can be added as needed
}
