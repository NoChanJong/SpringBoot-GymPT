package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.ShopReply;

public interface ShopReplyRepository extends CrudRepository<ShopReply, Integer >{
	
	@Transactional
	List<ShopReply> findBysSeq(@Param("sSeq") int sSeq);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ShopReply sr WHERE sr.sSeq = :sSeq AND sr.sRno = :sRno")
	void deleteBysSeq(@Param("sSeq") int sSeq, @Param("sRno") int sRno);

	
	// 리뷰달린 게시글 지우기
	@Transactional
	@Modifying
	void deleteBysSeq(int sSeq);
	

	

	
	


}
