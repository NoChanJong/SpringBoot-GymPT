package com.lec.service;

import java.util.List;

import com.lec.domain.Community;
import com.lec.domain.Member;

public interface CommunityService {

	long getTotalRowCount(Community community);
	Community getCommunity(Community community);
	List<Community> getCommunityList();
	void insertCommunity(Community community);
	void updateCommunity(Community community);
	void deleteCommunity(Community community);
	Community findCommunityById(Integer c_seq);
	int updateCount(Community community);
	
	List<Community> searchCommunityByKeyword(String keyword);
	
	// 좋아요
	void addLikeToCommunity(Integer c_seq, String memberId);
	void removeLikeFromCommunity(Integer c_seq, String memberId);
	int getLikeCountForCommunity(Integer c_seq);
	List<Community> getLikedCommunitiesForMember(String memberId);
}
