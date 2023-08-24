package com.lec.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lec.domain.Community;
import com.lec.domain.CommunityReply;
import com.lec.domain.Likes;
import com.lec.domain.Member;
import com.lec.persistence.CommunityReplyRepository;
import com.lec.persistence.CommunityRepository;
import com.lec.persistence.MemberRepository;
import com.lec.service.CommunityReplyService;


@Service
public class CommunityReplyServiceImpl implements CommunityReplyService {

	@Autowired
	private CommunityReplyRepository communityReplyRepository;
	
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private MemberRepository memberRepository;


	@Override
	public void deleteReply(Integer replyId) {
		Optional<CommunityReply> replyOptional = communityReplyRepository.findById(replyId);
	    if (replyOptional.isPresent()) {
	        CommunityReply reply = replyOptional.get();
	        
	        
	        communityReplyRepository.delete(reply);
	    }
	}


	@Override
	public CommunityReply createReply(String content, Community community, Member member) {
		CommunityReply reply = new CommunityReply();
        reply.setCrContent(content);
        reply.setCommunity(community);
        reply.setMember(member);

        return communityReplyRepository.save(reply);
	}


	@Override
	public CommunityReply getReplyById(Integer replyId) {
		Optional<CommunityReply> replyOptional = communityReplyRepository.findById(replyId);
        return replyOptional.orElse(null);
	}


	

}
