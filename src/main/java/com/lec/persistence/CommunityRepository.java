package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.Community;


@Repository
public interface CommunityRepository extends CrudRepository<Community, Integer> {
 
	List<Community> findAllByMemberId(String memberId);
	
	@Modifying
	@Transactional
	@Query("update Community c set c.c_cnt = c.c_cnt + 1 where c.c_seq = :c_seq")
	int updateCount(@Param("c_seq") Integer c_seq);
	
  @Query("SELECT c FROM Community c JOIN c.likes l WHERE l.member.id = :memberId")
  List<Community> findLikedCommunitiesByMemberId(@Param("memberId") String memberId);
	
}
