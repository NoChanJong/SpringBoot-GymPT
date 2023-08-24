package com.lec.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id")
    private ChatRoom roomId;

    @ManyToOne
    @JoinColumn(name = "sender", referencedColumnName = "id")
    private Member sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver", referencedColumnName = "id")
    private Member receiver;

    @Column(nullable = false)
    private String message;

    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Date createDate_c;
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean member1Read;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean member2Read;


    // Constructors, getters, setters, and other methods can be added as needed
}

