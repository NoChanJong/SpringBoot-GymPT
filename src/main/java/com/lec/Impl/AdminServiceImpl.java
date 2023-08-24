package com.lec.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lec.domain.PointList;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;
import com.lec.persistence.PointListRepository;
import com.lec.persistence.ShopOrderDetailRepository;
import com.lec.persistence.ShopOrderRepository;
import com.lec.service.AdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService{
	@Autowired
	private final ShopOrderRepository orderRepo;
	@Autowired
	private final ShopOrderDetailRepository orderDetailRepo;
	@Autowired
	private final PointListRepository pointListRepo;
	@Override
	public List<ShopOrder> orderList(ShopOrder order) {
		return orderRepo.findOrderList();
	}

	@Override
	public List<ShopOrder> getOrderInfo(int orderNo) {
	   return orderRepo.findOrderListByOrderNo(orderNo);
	}

	@Override
	public List<ShopOrderDetail> orderView(int orderNo) {
	    return orderDetailRepo.findByOrderNo(orderNo);
	}


	@Override
	public void updateDelivery(Integer orderNo, String delivery) {
	    ShopOrder order = orderRepo.findByOrderNo(orderNo);
	    order.setDelivery(delivery);
	    orderRepo.save(order);
	}

	@Override
	public List<PointList> pointList(PointList pointList) {
		return pointListRepo.findPointList();
	}

	@Override
	public void updatePoint(Integer listNo, String sendMoney) {
		PointList pointList = pointListRepo.findByListNo(listNo);
		pointList.setSendMoney(sendMoney);
		pointListRepo.save(pointList);
		
	}

	


}
