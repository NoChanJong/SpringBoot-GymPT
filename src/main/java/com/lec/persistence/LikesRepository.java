package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.domain.Community;
import com.lec.domain.Likes;
import com.lec.domain.Member;

public interface LikesRepository extends JpaRepository<Likes, Integer>{

	Likes findByCommunityAndMember(Community community, Member member);

	List<Likes> findByMember(Member member);
	
}
