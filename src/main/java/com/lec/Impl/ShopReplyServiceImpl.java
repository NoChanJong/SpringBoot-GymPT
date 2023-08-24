package com.lec.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lec.domain.ShopOrder;
import com.lec.domain.ShopReply;
import com.lec.persistence.ShopOrderRepository;
import com.lec.persistence.ShopReplyRepository;
import com.lec.service.ShopReplyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopReplyServiceImpl implements ShopReplyService {
	
	@Autowired
	private final ShopReplyRepository shopReplyRepository;
	
	@Autowired
	private final ShopOrderRepository orderRepo;
	
	@Override
	public List<ShopReply> replyList(int sSeq) throws Exception {
		List<ShopReply> replyList = shopReplyRepository.findBysSeq(sSeq);
		return replyList;
	}

	@Override
	public ShopReply getReply(int sSeq, int sRno) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeReply(ShopReply shopReply) {
		shopReplyRepository.save(shopReply);
		
	}

	@Override
	public void deleteReply(int sSeq, int sRno){
		shopReplyRepository.deleteBysSeq(sSeq, sRno);
		
	}

	@Override
	public void eraseReplyBySSeq(int sSeq) {
		shopReplyRepository.deleteBysSeq(sSeq);
		
	}
	@Override
    public boolean canWriteReply(String memberId, int sSeq) {
        // 주문 리스트를 가져옵니다.
        List<ShopOrder> orderList = orderRepo.findOrderListByMemberId(memberId);

        // 주문 리스트가 비어있는 경우 (주문 내역이 없는 경우) 작성 불가능
        if (orderList == null || orderList.isEmpty()) {
            return false;
        }

        // 주문리스트에서 해당 상품(sSeq)을 찾습니다.
        for (ShopOrder order : orderList) {
            // 주문번호가 0 미만일 경우, 해당 주문이 없는 것으로 판단합니다.
            if (order.getOrderNo() < 0) {
                continue;
            }

            // 주문번호가 일치하는 경우 해당 주문의 주문 상세 리스트에서 상품이 있는지 확인
            List<Integer> orderDetails = orderRepo.findOrderDetailsByOrderNo(order.getOrderNo());

            // 주문 상세 리스트가 비어있는 경우 (주문 상세 내역이 없는 경우) 상품이 없다고 판단합니다.
            if (orderDetails == null || orderDetails.isEmpty()) {
                return false;
            }

            // 주문 상세 리스트에서 해당 상품(sSeq)을 찾습니다.
            for (Integer s : orderDetails) {
                if (s != null && s == sSeq) {
                    return true; // 주문 상세 내역에 상품이 존재하면 true를 반환합니다.
                }
            }
        }

        return false; // 주문 내역에 상품이 없으면 작성 불가능
    }


}
