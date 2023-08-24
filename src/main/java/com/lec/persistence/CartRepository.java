package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.Cart;
import com.lec.domain.Shop;

public interface CartRepository extends CrudRepository<Cart, Integer>{

		List<Cart> findBysNameContainingIgnoreCase(String sName);
	    List<Cart> findByMemberIdContaining(String memberId);
	    
		@Transactional
		@Modifying
		@Query("DELETE FROM Cart c WHERE c.sSeq = :sSeq AND c.cartNo = :cartNo")
		void deleteById(@Param("sSeq") int sSeq, @Param("cartNo") int cartNo);
		
		Cart findBysSeqAndMemberId( @Param("sSeq")int sSeq, String memberId);
		
		@Transactional
		@Modifying
		@Query("DELETE FROM Cart c WHERE c.cartNo IN :cartIds")
		void deleteByIdIn(@Param("cartIds") List<Integer> cartIds);
		
		@Transactional
		@Modifying
		@Query("SELECT c FROM Cart c WHERE c.cartNo IN :cartIds")
		 List<Cart> findAllById(Iterable<Integer> cartIds); // 추가
		
		List<Cart> getCartListByMemberId(String memberId);
		List<Cart> findByMemberId(String memberId);
		
		@Transactional
		@Modifying
		@Query("DELETE FROM Cart c WHERE c.memberId = :memberId")
		void deleteByMemberId(String memberId);
}










