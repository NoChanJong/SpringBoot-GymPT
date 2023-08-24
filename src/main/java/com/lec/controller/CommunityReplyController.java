package com.lec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lec.domain.Community;
import com.lec.domain.CommunityReply;
import com.lec.domain.Member;
import com.lec.service.CommunityReplyService;
import com.lec.service.CommunityService;
import com.lec.service.MemberService;

@RestController
@RequestMapping("/api")
public class CommunityReplyController {

	@Autowired
	private CommunityReplyService communityReplyService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private MemberService memberService;
	
	@PostMapping("/{c_seq}/reply/create")
    public ResponseEntity<String> createReply(@PathVariable Integer c_seq,
            @RequestParam("content") String content, @RequestParam("memberId") String memberId) {
        Community community = communityService.findCommunityById(c_seq);
        if (community == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Community not found");
        }

        Member member = memberService.findMemberById(memberId); // Retrieve the Member entity
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }

        CommunityReply reply = communityReplyService.createReply(content, community, member);
        if (reply != null) {
            return ResponseEntity.ok("Reply created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create reply");
        }
    }
	 
	// Delete a reply
	@DeleteMapping("/{c_seq}/reply/{replyId}/delete")
	public ResponseEntity<String> deleteReply(@PathVariable Integer c_seq, @PathVariable Integer replyId) {
	    communityReplyService.deleteReply(replyId);
	    return ResponseEntity.ok("Reply deleted successfully");
	}
}
