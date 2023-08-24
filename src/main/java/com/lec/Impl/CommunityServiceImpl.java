package com.lec.Impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lec.domain.Community;
import com.lec.domain.Likes;
import com.lec.domain.Member;
import com.lec.persistence.CommunityReplyRepository;
import com.lec.persistence.CommunityRepository;
import com.lec.persistence.CustomCommunityRepository;
import com.lec.persistence.LikesRepository;
import com.lec.persistence.MemberRepository;
import com.lec.service.CommunityService;


@Service
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private LikesRepository likesRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private CommunityReplyRepository communityReplyRepository;
	
	@Autowired
	private CustomCommunityRepository customCommunityRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public long getTotalRowCount(Community community) {
		return communityRepository.count();
	}

	@Override
	public Community getCommunity(Community community) {
		Community board = communityRepository.findById(community.getC_seq()).orElse(null);
		if(board != null) {
			board.setC_cnt(board.getC_cnt() + 1);
			communityRepository.save(board);
		}
		return board;
	}
	
//	@Override
//	public List<Community> getCommunityList(Community community) {
//		return (List<Community>) communityRepository.findAll();
//	}

	   @Override
	    public void insertCommunity(Community community) {
	        // 커뮤니티 엔티티에 연결된 멤버 엔티티를 영속성 컨텍스트에 저장합니다.
	        Member member = community.getMember();
	        memberRepository.save(member);

	        // 멤버 엔티티를 저장한 후, 커뮤니티 엔티티에 해당 멤버 엔티티를 설정합니다.
	        communityRepository.save(community);
	    }

	@Override
	public void updateCommunity(Community community) {
		Community findCommunity = communityRepository.findById(community.getC_seq()).orElse(null);
		communityRepository.save(findCommunity);
	}

	@Override
	public void deleteCommunity(Community c_board) {
		communityRepository.deleteById(c_board.getC_seq());
	}

	@Override
	public void addLikeToCommunity(Integer c_seq, String memberId) {
	    Community community = communityRepository.findById(c_seq)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid community ID: " + c_seq));

	    Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

	    Likes existingLike = likesRepository.findByCommunityAndMember(community, member);
	    if (existingLike == null) {
	        Likes like = new Likes();
	        like.setCommunity(community);
	        like.setMember(member);
	        likesRepository.save(like);

	         community.setC_like(community.getC_like() + 1);
	        communityRepository.save(community);
	    }
	}

	@Override
	public void removeLikeFromCommunity(Integer c_seq, String memberId) {
	    Community community = communityRepository.findById(c_seq)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid community ID: " + c_seq));

	    Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

	    Likes like = likesRepository.findByCommunityAndMember(community, member);
	    if (like != null) {
	        likesRepository.delete(like);
	        
	         community.setC_like(community.getC_like() - 1);
	        communityRepository.save(community);
	    }
	}

	@Override
	public int getLikeCountForCommunity(Integer c_seq) {
		// c_seq에 해당하는 Community 객체 조회
	    Community community = communityRepository.findById(c_seq)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid c_seq: " + c_seq));

	    // Community 객체에서 좋아요 수를 가져옴
	    Integer likeCount = community.getC_like();
	    
	    // 좋아요 수가 null이면 0으로 초기화
	    if (likeCount == null) {
	        likeCount = 0;
	    }

	    return likeCount;
	}

	@Override
	public Community findCommunityById(Integer c_seq) {
		return communityRepository.findById(c_seq).orElse(null);
	}

	@Override
	public int updateCount(Community community) {
		return communityRepository.updateCount(community.getC_seq());
	}

	@Override
	public List<Community> searchCommunityByKeyword(String keyword) {
		return customCommunityRepository.findByTitleOrContentContaining(keyword);
	}

	@Override
	public List<Community> getCommunityList() {
		String jpql = "SELECT c FROM Community c ORDER BY c.c_seq DESC";
        TypedQuery<Community> query = entityManager.createQuery(jpql, Community.class);
        return query.getResultList();
	}
	@Override
	   public List<Community> getLikedCommunitiesForMember(String memberId) {
	       return communityRepository.findLikedCommunitiesByMemberId(memberId);
	}
}
