package com.lec.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Order;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.ShopOrder;


public interface ShopOrderRepository extends CrudRepository<ShopOrder, Integer>{

    @Transactional
    @Modifying
    @Query("SELECT o FROM ShopOrder o WHERE o.memberId = :memberId ORDER BY o.orderDate DESC")
    List<ShopOrder> findByMemberId(String memberId);
	      
	    @Transactional
	    @Modifying
	    @Query("SELECT o FROM ShopOrder o ORDER BY o.orderDate DESC")
	    List<ShopOrder> findOrderList();
	 
	 @Transactional
	 @Modifying
	 @Query("SELECT o FROM ShopOrder o WHERE o.orderNo = :orderNo")
	 List<ShopOrder> findOrderListByOrderNo(@Param("orderNo") int orderNo);

	ShopOrder findByOrderNo(int orderNo);

	// 주문 상세 리스트를 조회하는 메서드
    @Query("SELECT od.sSeq FROM ShopOrderDetail od WHERE od.orderNo = :orderNo")
    List<Integer> findOrderDetailsByOrderNo(int orderNo);

    @Transactional
    @Modifying
   @Query("SELECT o FROM ShopOrder o WHERE o.memberId = :memberId")
   List<ShopOrder> findOrderListByMemberId(String memberId);

	

}
