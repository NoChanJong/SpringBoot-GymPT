package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;

public interface ShopOrderDetailRepository extends CrudRepository<ShopOrderDetail, Integer>{

	List<ShopOrderDetail> findByOrderNo(int orderNo);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ShopOrderDetail od WHERE od.orderNo = :orderNo AND od.orderDetailNo = :orderDetailNo")
	void deleteByOrderNoAndOrderDatailNo(int orderNo, int orderDetailNo);

//	ShopOrderDetail findByOrderNoAndOrderDetailNoAndOrderStatus(int orderNo, int orderDetailNo, String orderStatus);

	ShopOrderDetail findByOrderNoAndOrderDetailNo(int orderNo, int orderDetailNo);

}
