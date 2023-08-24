package com.lec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.domain.AskBoard;
import com.lec.domain.Member;
import com.lec.domain.PointList;

public interface MemberService {
	
	long getTotalRowCount(Member member);
	Member getMember(Member member);
	Member getMemberById(String memberId);
	Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord);
	void insertMember(Member member);
	void updateMember(Member member);
	void deleteMember(Member member);
	void deleteMemberAndChatRooms(Member member);
	boolean existsById(String id);	
	
	// 포인트충전
	void insertPoint(PointList pointList);		
	// 포인트업데이트
	void updatePoint(Member member);
	
	
	// member list, search
	Member findByName(String name);
	Page<Member> selectMember(Pageable pageable, String searchType, String searchWord);
	Member findMemberById(String memberId);
}