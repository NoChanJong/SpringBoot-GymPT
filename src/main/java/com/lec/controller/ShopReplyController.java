package com.lec.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.Member;
import com.lec.domain.Shop;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopReply;
import com.lec.domain.UserPrincipal;
import com.lec.service.ShopReplyService;
import com.lec.service.ShopService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"member"})
public class ShopReplyController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopReplyService shopReplyService;
	
	@Autowired
	Environment environment;
	
	@Value("${path.rUpload}")
	public String uploadFolder;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}
	@ModelAttribute("shop")
	public Shop setShop() {
		return new Shop();
	}
	
	@PostMapping("/writeReply")
    public String postReply(
                            @ModelAttribute("member") Member member,
                            @ModelAttribute("shop") Shop shop, 
                            HttpServletRequest request,
                            ShopOrder order,
                            @ModelAttribute("shopReply") ShopReply shopReply,Model model) throws Exception {

        List<MultipartFile> uploadFiles = shopReply.getUploadFiles();
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            StringBuilder srUploads = new StringBuilder();

            for (MultipartFile uploadFile : uploadFiles) {
                if (!uploadFile.isEmpty()) {
                    String srUpload = uploadFile.getOriginalFilename();
                    uploadFile.transferTo(new File(uploadFolder + srUpload));
                    if (srUploads.length() > 0) {
                        srUploads.append(",");
                    }
                    srUploads.append(srUpload);
                }
            }

            shopReply.setSrUpload(srUploads.toString());
        }

        
           String sSeqParam = request.getParameter("s_seq"); // 상품 sSeq을 가져옵니다.
        int sSeq = Integer.parseInt(sSeqParam); // 문자열을 int로 변환합니다.
        shopReply.setSSeq(sSeq); // sSeq 값을 설정합니다.
        
        boolean canWriteReply = shopReplyService.canWriteReply(shopReply.getMemberId(), sSeq);
        if (canWriteReply) {
            // 댓글이 작성 가능한 경우
            shopReplyService.writeReply(shopReply);
            model.addAttribute("replyMessage", "댓글이 작성되었습니다");
        } else {
            // 댓글이 작성 불가능한 경우
            model.addAttribute("replyMessage", "구매 먼저 해주세요");
        }

        return "redirect:shop/shopBoardView?sSeq=" + sSeq + "&message=" + URLEncoder.encode(model.getAttribute("replyMessage").toString(), "UTF-8");
    }


	
	@GetMapping("/deleteReply")
	public String deleteReply(Authentication authentication,@ModelAttribute("shop") Shop shop, @ModelAttribute("shopReply") ShopReply shopReply, @ModelAttribute("member") Member member,
			int sSeq, int sRno, Model model) throws Exception {
		
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
					UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
					// 사용자의 아이디를 가져옵니다.
					String userId = userPrincipal.getUsername();
					if(userId == null) {
						return "redirect:/login";
					} 
		shopReplyService.deleteReply(sSeq, sRno);
		return "redirect:shop/shopBoardView?sSeq= "+ shop.getSSeq();
	}

	
	
}








