package com.lec.service;

import com.lec.domain.Community;
import com.lec.domain.CommunityReply;
import com.lec.domain.Member;

public interface CommunityReplyService {

	CommunityReply createReply(String content, Community community, Member member);

    void deleteReply(Integer replyId);

    CommunityReply getReplyById(Integer replyId);
	
}
