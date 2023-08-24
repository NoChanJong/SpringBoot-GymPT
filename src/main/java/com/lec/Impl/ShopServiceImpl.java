package com.lec.Impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.domain.Cart;
import com.lec.domain.Shop;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;
import com.lec.domain.ShopReply;
import com.lec.persistence.CartRepository;
import com.lec.persistence.ShopOrderDetailRepository;
import com.lec.persistence.ShopOrderRepository;
import com.lec.persistence.ShopReplyRepository;
import com.lec.persistence.ShopRepository;
import com.lec.service.ShopService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShopServiceImpl implements ShopService{
	
	@Autowired
	private final ShopRepository shopRepo;
	@Autowired
	private final ShopReplyRepository shopReplyRepo;
	@Autowired
	private final CartRepository cartRepo;
	@Autowired
	private final ShopOrderRepository orderRepo;
	@Autowired
	private final ShopOrderDetailRepository orderDetailRepo;

	
	@Value("${path.upload}")
	public String uploadFolder;
	
	
	@Override
	public void register(Shop shop) {
		shopRepo.save(shop);
	}
	@Override
	public Shop shopBoardView(Shop shop) {
		Optional<Shop> findShopBoard = shopRepo.findById(shop.getSSeq());
		if(findShopBoard.isPresent())
			return findShopBoard.get();
		else return null;
	}
	@Override
	public Shop shopBoardViewBysSeq(int sSeq) {
	    Optional<Shop> findShopBoard = shopRepo.findById(sSeq);
	    return findShopBoard.orElse(null);
	}
	
	 public Page<Shop> ShopBoardList(Pageable pageable, String searchType, String searchWord, String sContent, String sName, String sCate, String memberId) {
		 Page<Shop> page;
		    if (searchType.equalsIgnoreCase("식품")) {
		        page = shopRepo.findBysCateContainingAndsNameContainingAllIgnoreCase("식품", searchWord, pageable);
		    } else if (searchType.equalsIgnoreCase("의류")) {
		        page = shopRepo.findBysCateContainingAndsNameContainingAllIgnoreCase("의류", searchWord, pageable);
		    } else if (searchType.equalsIgnoreCase("장비")) {
		        page = shopRepo.findBysCateContainingAndsNameContainingAllIgnoreCase("장비", searchWord, pageable);
		    } else {
		        page = shopRepo.findBysNameContainingIgnoreCase(searchWord, pageable);
		    }
		    return page;
		
	    }

	 @Override
		public long getTotalRowCount(Shop shop) {
			return shopRepo.count();
		}
		
	 @Override
	 public void updateShop(Shop shop) {
	     // 기존의 상품 정보를 가져옵니다.
	     Shop existingShop = shopRepo.findById(shop.getSSeq()).orElse(null);
	     if (existingShop != null) {
	         // 수정할 필드 값들을 새로운 값으로 업데이트합니다.
	         existingShop.setSUpload(shop.getSUpload());
	         existingShop.setSUploadThumb(shop.getSUploadThumb());
	         existingShop.setSUploadContent(shop.getSUploadContent());
	         existingShop.setSCate(shop.getSCate());
	         existingShop.setSContent(shop.getSContent());
	         existingShop.setSName(shop.getSName());
	         existingShop.setSPrice(shop.getSPrice());
	         existingShop.setSAmount(shop.getSAmount());
	         shopRepo.save(existingShop);
	     }
	 }

	@Override
	public void deleteShop(Shop shop) {
		shopRepo.deleteById(shop.getSSeq());
		
	}
	@Override
	public void addCart(Cart cart) {
		cartRepo.save(cart);
		
	}
	@Override
	 public List<Cart> cartList(String searchType, String searchWord, String sName, String memberId) {
        if (searchType.equalsIgnoreCase("sName")) {
        	return cartRepo.findBysNameContainingIgnoreCase(sName);
        }else {
        	return cartRepo.findByMemberIdContaining(memberId);
        }
    

}
	@Override
	public void deleteCart(int sSeq,int cartNo) {
		cartRepo.deleteById(sSeq, cartNo);
		
	}
	@Override
	public boolean isProductInCart(String memberId, int sSeq) {
		 Cart cart = cartRepo.findBysSeqAndMemberId(sSeq, memberId);
		    return cart != null; // 장바구니 항목이 조회되었다면 상품이 이미 장바구니에 있는 것입니다.
	}
	@Override
	public void deleteSelectedCart(List<Integer> cartIds) {
	    cartRepo.deleteByIdIn(cartIds);
	}
	@Override
	public void orderInfo(ShopOrder order) {
		orderRepo.save(order);
		
	}
	@Override
	public void orderInfoDetails(ShopOrderDetail orderDetail) {
		orderDetailRepo.save(orderDetail);
		
	}
	@Override
	public BigDecimal calculateTotalPrice(String memberId) {
		 List<Cart> cartList = cartRepo.getCartListByMemberId(memberId);
		    BigDecimal totalPrice = BigDecimal.ZERO;

		    for (Cart cart : cartList) {
		        BigDecimal itemPrice = cart.getSPrice().multiply(new BigDecimal(cart.getSAmount()));
		        totalPrice = totalPrice.add(itemPrice);
		    }

		    return totalPrice;
	}
	@Override
	public List<Cart> getCartSSeqAndSAmount(String memberId) {
		return cartRepo.findByMemberId(memberId);
	}
	@Override
	public void cartAllDelete(String memberId) {
		cartRepo.deleteByMemberId(memberId);
		
	}
	@Override
	public List<ShopOrder> orderList(String memberId) {
		return orderRepo.findByMemberId(memberId);
	}
	@Override
	public List<ShopOrderDetail> orderView(int orderNo) {

		return orderDetailRepo.findByOrderNo(orderNo);
	}
	@Override
	public List<ShopOrder> getOrderInfo(int orderNo) {
		 return orderRepo.findOrderListByOrderNo(orderNo);
	}
	@Override
	public BigDecimal averageSRating(int sSeq) {
	    List<ShopReply> replyList = shopReplyRepo.findBysSeq(sSeq);
	    BigDecimal sumSRating = BigDecimal.ZERO;

	    for (ShopReply shopReply : replyList) {
	        sumSRating = sumSRating.add(shopReply.getSRating());
	    }

	    if (!replyList.isEmpty()) {
	        BigDecimal averageSRating = sumSRating.divide(new BigDecimal(replyList.size()), RoundingMode.HALF_UP);
	        return averageSRating;
	    } else {
	        return BigDecimal.ZERO;
	    }
	}
	@Override
	public void deleteOrder(int orderNo, int orderDetailNo, String orderStatus) {
		ShopOrderDetail orderDetail = orderDetailRepo.findByOrderNoAndOrderDetailNo(orderNo, orderDetailNo);
		orderDetail.setOrderStatus(orderStatus);
		orderDetailRepo.save(orderDetail);
	}
	@Override
	public void updatePoint(BigDecimal memberPoint, String memberId) {
	    shopRepo.save(memberPoint, memberId);
	}
	@Override
	public void updateSAmount(int sAmount, int sSeq) {
		shopRepo.save(sAmount, sSeq);
		
	}

	


	
		
	}




