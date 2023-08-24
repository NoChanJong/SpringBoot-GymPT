package com.lec.service;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.criteria.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.domain.Cart;
import com.lec.domain.Member;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;
import com.lec.domain.Shop;

@Service
public interface ShopService {

	 void register(Shop shop);
	 Page<Shop> ShopBoardList(Pageable pageable, String searchType, String searchWord, String sContent, String sName, String sCate, String memberId);
	 long getTotalRowCount(Shop shop);
	 Shop shopBoardView(Shop shop);
	 void updateShop(Shop shop);
	 void deleteShop(Shop shop);
	 void addCart(Cart cart);
	 void deleteCart(int sSeq,int cartNo);
	 List<Cart> cartList(String searchType, String searchWord, String sName, String memberId);
	boolean isProductInCart(String memberId, int sSeq);
	void deleteSelectedCart(List<Integer> cartIds);

	  void orderInfo(ShopOrder order);
	  void orderInfoDetails(ShopOrderDetail orderDetail);
	BigDecimal calculateTotalPrice(String id);
	List<Cart> getCartSSeqAndSAmount(String memberId);
	
	void cartAllDelete(String memberId);
	List<ShopOrder> orderList(String memberId);
	
	// 특정 주문 목록
	List<ShopOrderDetail> orderView(int orderNo);
	// 주문 상세보기에 보여질 메인주문정보
	 List<ShopOrder> getOrderInfo(int orderNo);
	 BigDecimal averageSRating(int sSeq);
	// 주문 취소
	 void deleteOrder(int orderNo, int orderDetailNo, String orderStatus);
	 // 결제 시 포인트 업데이트
	 void updatePoint(BigDecimal memberPoint, String memberId);
	 // 결제 시 재고 업데이트
	 void updateSAmount(int sAmount, int sSeq);
	 Shop shopBoardViewBysSeq(int sSeq);
	 


}
