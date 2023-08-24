package com.lec.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.lec.domain.Member;
import com.lec.domain.PointList;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;
import com.lec.domain.UserPrincipal;
import com.lec.service.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"member"})
public class AdminController {
	
	@Autowired
	AdminService adminService;
	@Autowired
	Environment environment;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}
	
	 @RequestMapping("memberOrderList")
	   public String getOrderList(Authentication authentication,HttpSession session, ShopOrder order, Model model, @ModelAttribute("member") Member member) throws Exception {
			// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
		   List<ShopOrder> orderList = adminService.orderList(order);
		   model.addAttribute("orderList", orderList);
		   
		   return "admin/memberOrderList";
}
	 @RequestMapping("memberOrderView")
	 public String getOrderView(Authentication authentication,HttpSession session, @ModelAttribute("shopOrder") ShopOrder order, ShopOrderDetail orderDetail, Model model, @ModelAttribute("member") Member member, @RequestParam("no") Integer orderNo) throws Exception {
	    
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
		 
			if(userId == null) {
				return "redirect:/login";
			} 
	     order.setMemberId(member.getId());
	     order.setOrderNo(orderNo);
	    
	     orderDetail.setOrderNo(orderNo);
	    
//	     List<ShopOrder> orderList = adminService.orderList(order);
//	     model.addAttribute("orderList", orderList);
	     
	     List<ShopOrder> orderInfo = adminService.getOrderInfo(orderNo); // 주문 정보 가져오기
	     model.addAttribute("orderInfo", orderInfo);
	    
	     List<ShopOrderDetail> orderView = adminService.orderView(orderNo); // 특정 주문번호의 상세 정보 가져오기
	     model.addAttribute("orderView", orderView);
	    
	     return "admin/memberOrderView";
	 }
		
	 @PostMapping("/deliveryChange")
	 public String deliveryChange(Authentication authentication,HttpServletRequest request, ShopOrderDetail orderDetail, ShopOrder order, Model model, @ModelAttribute("member") Member member) {
	     
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
		 
		 
			if(userId == null) {
				return "redirect:/login";
			} 

	     String orderNoString = request.getParameter("orderNo"); // orderNo를 문자열로 가져옵니다.
	     Integer orderNo = Integer.valueOf(orderNoString); // 문자열을 Integer로 변환합니다.

	     String delivery = request.getParameter("delivery"); // 상품 정보를 가져옵니다.
	     order.setDelivery(delivery); // delivery 값을 설정합니다.
	     adminService.updateDelivery(orderNo, delivery); // orderNo와 delivery를 전달하여 업데이트합니다.
	     return "redirect:memberOrderList";
	 }
	 
	 @RequestMapping("memberPointList")
	 public String getPointList(Authentication authentication,HttpSession session, PointList pointList, Model model, @ModelAttribute("member") Member member) throws Exception {
		   
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			if(userId == null) {
				return "redirect:/login";
			} 
		   List<PointList> list = adminService.pointList(pointList);
		   model.addAttribute("list", list);
		   
		   return "admin/memberPointList";
	 }
	 @PostMapping("/sendMoneyChange")
	 public String sendMoneyChange(Authentication authentication,HttpServletRequest request, PointList pointList, Model model, @ModelAttribute("member") Member member) {
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
		 
		 if(userId == null) {
				return "redirect:/login";
			} 
	     
	     String listNoString = request.getParameter("listNo"); // orderNo를 문자열로 가져옵니다.
	     Integer listNo = Integer.valueOf(listNoString); // 문자열을 Integer로 변환합니다.

	     String sendMoney = request.getParameter("send_money"); // sendMoney를 가져옵니다.
	     pointList.setSendMoney(sendMoney); // sendMoney 값을 설정합니다.
	     
	     adminService.updatePoint(listNo, sendMoney); // orderNo와 delivery를 전달하여 업데이트합니다.
	     return "redirect:memberPointList";
	 }

}







