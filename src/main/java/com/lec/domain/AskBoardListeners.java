package com.lec.domain;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class AskBoardListeners {

//    @PostLoad
//    public void postLoad(Board board) {
//        System.out.println("post load: {}" +  board);
//    }
    
    @PrePersist
    public void prePersist(AskBoard board) {
        System.out.println("pre persist: {}" + board);
    }
    
    @PostPersist
    public void postPersist(AskBoard board) {
        System.out.println("post persist: {}" + board);
    }
    
    @PreUpdate
    public void preUpdate(AskBoard board) {
        System.out.println("pre update: {}" + board);
    }
    
    @PostUpdate
    public void postUpdate(AskBoard board) {
        System.out.println("post update: {}" +  board);
    }
    
    @PreRemove
    public void preRemove(AskBoard board) {
        System.out.println("pre remove: {}" +  board);
    }
    
    @PostRemove
    public void postRemove(AskBoard board) {
        System.out.println("post remove: {}" +  board);
    }
}

