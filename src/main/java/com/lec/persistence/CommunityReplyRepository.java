package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.domain.CommunityReply;
import com.lec.domain.Member;

public interface CommunityReplyRepository extends JpaRepository<CommunityReply, Integer>{

	List<CommunityReply> findByMember(Member member);


}
