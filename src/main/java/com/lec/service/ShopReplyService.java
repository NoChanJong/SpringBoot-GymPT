package com.lec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lec.domain.ShopReply;

@Service
public interface ShopReplyService {
	
	List<ShopReply> replyList(int sSeq) throws Exception; 
	ShopReply getReply(int sSeq, int sRno);
	
	void writeReply(ShopReply shopReply);
	void deleteReply(int sSeq, int sRno) throws Exception;
	void eraseReplyBySSeq(int sSeq);
//	Long getReplyCountBySSeq(int sSeq);
	boolean canWriteReply(String memberId, int sSeq);
	
}
