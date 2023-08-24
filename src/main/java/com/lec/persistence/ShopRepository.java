package com.lec.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.Shop;

public interface ShopRepository extends CrudRepository<Shop, Integer> {
   
    
    @Query("UPDATE Shop s SET s.sName = :sName, s.sPrice = :sPrice, s.sContent = :sContent, s.sCate = :sCate WHERE s.sSeq = :sSeq")
    @Transactional
    @Modifying
    void updateShopDetails(@Param("sName") String sName, @Param("sPrice") BigDecimal sPrice, @Param("sContent") String sContent, @Param("sCate") String sCate, @Param("sSeq") int sSeq);
	
    @Query("UPDATE Member m SET m.point = :point WHERE m.id = :memberId")
    @Transactional
    @Modifying
    void save(@Param("point") BigDecimal point, @Param("memberId") String memberId);
    Page<Shop> findBysNameContainingIgnoreCase(String sName, Pageable pageable);
    Page<Shop> findBysCateContaining(String sCate, Pageable pageable);
    
    @Query("SELECT s FROM Shop s WHERE s.sCate LIKE %:sCate% AND s.sName LIKE %:sName%")
    Page<Shop> findBysCateContainingAndsNameContainingAllIgnoreCase(@Param("sCate") String sCate, @Param("sName") String sName, Pageable pageable);
  
    
    @Query("UPDATE Shop s SET s.sAmount = :sAmount WHERE s.sSeq = :sSeq")
    @Transactional
    @Modifying
	void save(@Param("sAmount") int sAmount, @Param("sSeq") int sSeq);
   }
	



