package com.lec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lec.domain.PointList;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;

@Service
public interface AdminService {
	
	List<ShopOrder> orderList(ShopOrder order);
	List<ShopOrderDetail> orderView(int orderNo);
	List<ShopOrder> getOrderInfo(int orderNo);
//	void updateDelivery(ShopOrder order);
	void updateDelivery(Integer orderNo, String delivery);
	List<PointList> pointList(PointList pointList);
	void updatePoint(Integer listNo, String sendMoney);
}
