package com.lec.controller;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.Cart;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.domain.Shop;
import com.lec.domain.ShopOrder;
import com.lec.domain.ShopOrderDetail;
import com.lec.domain.ShopReply;
import com.lec.domain.UserPrincipal;
import com.lec.service.MemberService;
import com.lec.service.ShopReplyService;
import com.lec.service.ShopService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@SessionAttributes({"member"})
public class ShopController {
	
	
	@Autowired
	private MemberService memberService;

	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ShopReplyService shopReplyService;
	
	@Autowired
	Environment environment;
	
	@Value("${path.upload}")
	public String uploadFolder;
	@Value("${path.download}")
	private String downloadFolder;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}
	PagingInfo pagingInfo = new PagingInfo();
	
	 @RequestMapping("/shopBoardList")
	    public String getShopBoardList(
	              @RequestParam(value = "searchType", defaultValue = "") String searchType,
	              @RequestParam(value = "searchWord", defaultValue = "") String searchWord,
	              @RequestParam(value = "sName", defaultValue = "") String sName,
	              @RequestParam(value = "sCate", defaultValue = "") String sCate,
	              @RequestParam(value = "sContent", defaultValue = "") String sContent,
	              @RequestParam(value = "memberId", defaultValue = "") String memberId,
	              @RequestParam(defaultValue="0") int curPage,
	            @RequestParam(defaultValue="8") int rowSizePerPage,
	            
	              Model model) throws Exception {
	          Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Direction.DESC, "sSeq");
	         Page<Shop> pagedResult = shopService.ShopBoardList(pageable,  searchType,  searchWord,  sContent,  sName,  sCate,  memberId);
	      
	         int totalRowCount  = pagedResult.getNumberOfElements();
	         int totalPageCount = pagedResult.getTotalPages();
	         int pageSize       = pagingInfo.getPageSize();
	         int startPage      = curPage / pageSize * pageSize + 1;
	         int endPage        = startPage + pageSize - 1;
	         endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;
	      
	         pagingInfo.setCurPage(curPage);
	         pagingInfo.setTotalRowCount(totalRowCount);
	         pagingInfo.setTotalPageCount(totalPageCount);
	         pagingInfo.setStartPage(startPage);
	         pagingInfo.setEndPage(endPage);
	         pagingInfo.setSearchType(searchType);
	         pagingInfo.setSearchWord(searchWord);
	         pagingInfo.setRowSizePerPage(rowSizePerPage);
	         model.addAttribute("pagingInfo", pagingInfo);

	         model.addAttribute("pagedResult", pagedResult);
	         model.addAttribute("pageable", pageable);
	         model.addAttribute("cp", curPage);
	         model.addAttribute("sp", startPage);
	         model.addAttribute("ep", endPage);
	         model.addAttribute("ps", pageSize);
	         model.addAttribute("rp", rowSizePerPage);
	         model.addAttribute("tp", totalPageCount);
	         model.addAttribute("st", searchType);
	         model.addAttribute("sw", searchWord);   
	       
	       List<Shop> shopBoardList = pagedResult.getContent();
	          for (Shop shop : shopBoardList) {
	              BigDecimal averageSRating = shopService.averageSRating(shop.getSSeq());
	              shop.setSRating(averageSRating);
	          }
	          model.addAttribute("list", shopBoardList);

	          return "shop/shopBoardList";
	      }
	 
	 @GetMapping("/shop/shopBoardView")
	 public String shopBoardView(Authentication authentication, @ModelAttribute("shop") Shop shop, Model model, @ModelAttribute("ShopReply") ShopReply shopReply,@RequestParam("sSeq") int sSeq) throws Exception {
	 	// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	 	UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	 	// 사용자의 아이디를 가져옵니다.
	 	String userId = userPrincipal.getUsername();
	 	
	 	if(userId == null) {
	 		return "redirect:/login";
	 	} 

	 	BigDecimal averageSRating = shopService.averageSRating(shop.getSSeq());
	 	model.addAttribute("averageSRating", averageSRating);
	 	model.addAttribute("shop", shopService.shopBoardView(shop));
	 	model.addAttribute("uploadPath", uploadFolder); // path.upload를 uploadPath로 추가

	 	if(userId == null) {
	 		return "redirect:/login";
	 	} 

	 	List<ShopReply> replyList = null;
	 	model.addAttribute("shopReply", shopReplyService.replyList(sSeq));
	 	return "shop/shopBoardView";
	 }

	 
	
	@GetMapping("/shopBoardRegister")
	public String shopBoardRegisterView(Authentication authentication) {
	    // 로그인한 사용자의 'ROLE_ADMIN' 권한 확인
	    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    boolean hasRoleAdmin = authorities.stream()
	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

	    if (!hasRoleAdmin) {
	        // 'ROLE_ADMIN' 권한이 없는 경우
	        return "redirect:login";
	    }
	    
	    return "shop/shopBoardRegister";
	}

	@PostMapping("/shopBoardRegister")
	public String postShopRegister(Authentication authentication, @ModelAttribute("shop") Shop shop,Member member ,HttpServletRequest request) throws Exception {
	    // 로그인한 사용자의 정보 가져오기
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		String userId = userPrincipal.getUsername();
		
		  if(userId == null) {
		 		return "redirect:/login";
		 	} 
	    // 상품 정보 이미지들
	    List<MultipartFile> uploadFiles = shop.getUploadFiles();
	    if (uploadFiles != null && !uploadFiles.isEmpty()) {
	        StringBuilder sUploads = new StringBuilder();
	        
	        for (MultipartFile uploadFile : uploadFiles) {
	            if (!uploadFile.isEmpty()) {
	                String sUpload = uploadFile.getOriginalFilename();
	                uploadFile.transferTo(new File(uploadFolder + sUpload));
	                if (sUploads.length() > 0) {
	                    sUploads.append(",");
	                }
	                sUploads.append(sUpload);
	            }
	        }
	        
	        shop.setSUpload(sUploads.toString());
	    }
	    // 상품 리스트 썸네일
	    List<MultipartFile> uploadFilesThumb = shop.getUploadFilesThumb();
	    if (uploadFilesThumb != null && !uploadFilesThumb.isEmpty()) {
	        StringBuilder sUploadsThumb = new StringBuilder();
	        
	        for (MultipartFile uploadFileThumb : uploadFilesThumb) {
	            if (!uploadFileThumb.isEmpty()) {
	                String sUploadThumb = uploadFileThumb.getOriginalFilename();
	                uploadFileThumb.transferTo(new File(uploadFolder + sUploadThumb));
	                if (sUploadsThumb.length() > 0) {
	                	sUploadsThumb.append(",");
	                }
	                sUploadsThumb.append(sUploadThumb);
	            }
	        }
	        
	        shop.setSUploadThumb(sUploadsThumb.toString());
	    }
	    
	 // 상품 컨텐츠 정보 이미지
	    List<MultipartFile> uploadFilesContent = shop.getUploadFilesContent();
	    if (uploadFilesContent != null && !uploadFilesContent.isEmpty()) {
	        StringBuilder sUploadsContent = new StringBuilder();
	        
	        for (MultipartFile uploadFileContent : uploadFilesContent) {
	            if (!uploadFileContent.isEmpty()) {
	                String sUploadContent = uploadFileContent.getOriginalFilename();
	                uploadFileContent.transferTo(new File(uploadFolder + sUploadContent));
	                if (sUploadsContent.length() > 0) {
	                	sUploadsContent.append(",");
	                }
	                sUploadsContent.append(sUploadContent);
	            }
	        }
	        shop.setSUploadContent(sUploadsContent.toString());
	    }
	    shop.setMemberId(userId);// memberId 값을 설정합니다.
	    String sCate = request.getParameter("s_cate"); // 선택한 카테고리 값을 가져옵니다.
	    shop.setSCate(sCate); // sCate 값을 설정합니다.
	    String sContent = request.getParameter("s_content"); // 상품 정보를 가져옵니다.
	    shop.setSContent(sContent); // sContent 값을 설정합니다.
	    String sName = request.getParameter("s_name"); // 상품 이름을 가져옵니다.
	    shop.setSName(sName); // sName 값을 설정합니다.
	    
	    String sPriceString = request.getParameter("s_price"); // 상품 가격을 문자열로 가져옵니다.
	    BigDecimal sPrice = new BigDecimal(sPriceString); // 문자열을 BigDecimal로 변환합니다.
	    shop.setSPrice(sPrice); // sPrice 값을 설정합니다.
	    
	    String sAmountString = request.getParameter("s_amount"); // 상품 수량을 문자열로 가져옵니다.
	    int sAmount = Integer.parseInt(sAmountString); // 문자열을 int로 변환합니다.
	    shop.setSAmount(sAmount); // sAmount 값을 설정합니다.

	    shopService.register(shop);
	    return "redirect:shopBoardList";
	}



	   @RequestMapping("/shop/download")
	   public void download(HttpServletRequest req, HttpServletResponse res) throws Exception {    
		   req.setCharacterEncoding("utf-8");
		    String sUpload = req.getParameter("fn");
		    
		    String fromPath = uploadFolder + sUpload;
		    String toPath = downloadFolder + sUpload;
	   
	      byte[] b = new byte[4096];
	      File f = new File(toPath);
	      FileInputStream fis = new FileInputStream(fromPath);
	      
	      String sMimeType = req.getSession().getServletContext().getMimeType(fromPath); // mimetype = file type : pdf, exe, txt.... 
	      if(sMimeType == null) sMimeType = "application/octet-stream";
	      
	      String sEncoding = new String(sUpload.getBytes("utf-8"), "8859_1");
	      String sEncoding1 = URLEncoder.encode(sUpload, "utf-8");
	      
	      res.setContentType(sMimeType);
	      res.setHeader("Content-Transfer-Encoding", "binary");
	      res.setHeader("Content-Disposition", "attachment; filename = " + sEncoding1);
	         
	      int numRead;
	      ServletOutputStream os = res.getOutputStream();
	   
	      while((numRead=fis.read(b, 0, b.length)) != -1 ) {
	         os.write(b, 0, numRead);
	      }
	      
	      os.flush();
	      os.close();
	      fis.close();
	      
	      // return "redirect:getBoardList";
	   }
	   @GetMapping("/updateShop")
		public String getUpdateShop(Authentication authentication,@ModelAttribute("member")Member member, @ModelAttribute("shop") Shop shop, Model model, @ModelAttribute("ShopReply") ShopReply shopReply,@RequestParam("sSeq") int sSeq) {
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
					UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
					// 사용자의 아이디를 가져옵니다.
					String userId = userPrincipal.getUsername();
					
					if(userId == null) {
						return "redirect:/login";
					} 
			model.addAttribute("shop", shopService.shopBoardView(shop));
			model.addAttribute("uploadPath", uploadFolder); // path.upload를 uploadPath로 추가
			return "shop/updateShop";
		}
	   @PostMapping("/updateShop")
	      public String updateShop(Authentication authentication,@ModelAttribute("member") Member member, @ModelAttribute("shop") Shop shop, HttpServletRequest request,
	            @RequestParam("existingUploadFiles") String existingUploadFiles,
	            @RequestParam("existingUploadFilesThumb") String existingUploadFilesThumb,
	               @RequestParam("existingUploadFilesContent") String existingUploadFilesContent) throws Exception {
	         // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	         UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	         // 사용자의 아이디를 가져옵니다.
	         String userId = userPrincipal.getUsername();
	         
	         if(userId == null) {
	            return "redirect:/login";
	         } 
	         // 기존 이미지 파일명 리스트를 가져옵니다.
	         List<String> existingList = new ArrayList<>(Arrays.asList(existingUploadFiles.split(",")));

	         // 기존 썸네일 이미지 파일명 리스트를 가져옵니다.
	         List<String> existingThumbList = new ArrayList<>(Arrays.asList(existingUploadFilesThumb.split(",")));

	         // 기존 컨텐츠 이미지 파일명 리스트를 가져옵니다.
	         List<String> existingContentList = new ArrayList<>(Arrays.asList(existingUploadFilesContent.split(",")));

	         // 새로운 이미지를 처리합니다.
	         List<MultipartFile> uploadFiles = shop.getUploadFiles();
	         if (uploadFiles != null && !uploadFiles.isEmpty()) {
	             for (MultipartFile uploadFile : uploadFiles) {
	                 if (!uploadFile.isEmpty()) {
	                     String sUpload = uploadFile.getOriginalFilename();
	                     uploadFile.transferTo(new File(uploadFolder + sUpload));

	                     if (!existingList.contains(sUpload)) {
	                         existingList.add(sUpload);
	                     }
	                 }
	             }
	         }

	         // 새로운 썸네일 이미지를 처리합니다.
	         List<MultipartFile> uploadFilesThumb = shop.getUploadFilesThumb();
	         if (uploadFilesThumb != null && !uploadFilesThumb.isEmpty()) {
	             for (MultipartFile uploadFileThumb : uploadFilesThumb) {
	                 if (!uploadFileThumb.isEmpty()) {
	                     String sUploadThumb = uploadFileThumb.getOriginalFilename();
	                     uploadFileThumb.transferTo(new File(uploadFolder + sUploadThumb));

	                     if (!existingThumbList.contains(sUploadThumb)) {
	                         existingThumbList.add(sUploadThumb);
	                     }
	                 }
	             }
	         }

	         // 새로운 컨텐츠 이미지를 처리합니다.
	         List<MultipartFile> uploadFilesContent = shop.getUploadFilesContent();
	         if (uploadFilesContent != null && !uploadFilesContent.isEmpty()) {
	             for (MultipartFile uploadFileContent : uploadFilesContent) {
	                 if (!uploadFileContent.isEmpty()) {
	                     String sUploadContent = uploadFileContent.getOriginalFilename();
	                     uploadFileContent.transferTo(new File(uploadFolder + sUploadContent));

	                     if (!existingContentList.contains(sUploadContent)) {
	                         existingContentList.add(sUploadContent);
	                     }
	                 }
	             }
	         }

	         // 기존 이미지와 썸네일 이미지, 컨텐츠 이미지를 업데이트합니다.
	         String finalSUploads = existingList.isEmpty() ? "" : String.join(",", existingList);
	         if (!finalSUploads.isEmpty() && finalSUploads.charAt(0) == ',') {
	             finalSUploads = finalSUploads.substring(1);
	         }
	         shop.setSUpload(finalSUploads);


	         String finalSUploadsThumb = existingThumbList.isEmpty() ? "" : String.join(",", existingThumbList);
	         if (!finalSUploadsThumb.isEmpty() && finalSUploadsThumb.charAt(0) == ',') {
	             finalSUploadsThumb = finalSUploadsThumb.substring(1);
	         }
	         shop.setSUploadThumb(finalSUploadsThumb);

	         String finalSUploadsContent = existingContentList.isEmpty() ? "" : String.join(",", existingContentList);
	         if (!finalSUploadsContent.isEmpty() && finalSUploadsContent.charAt(0) == ',') {
	             finalSUploadsContent = finalSUploadsContent.substring(1);
	         }
	         shop.setSUploadContent(finalSUploadsContent);
		       
		       
		       shop.setMemberId(member.getId()); // memberId 값을 설정합니다.
		       String sCate = request.getParameter("s_cate"); // 선택한 카테고리 값을 가져옵니다.
		       shop.setSCate(sCate); // sCate 값을 설정합니다.
		       String sContent = request.getParameter("s_content"); // 상품 정보를 가져옵니다.
		       shop.setSContent(sContent); // sContent 값을 설정합니다.
		       String sName = request.getParameter("s_name"); // 상품 이름을 가져옵니다.
		       shop.setSName(sName); // sName 값을 설정합니다.

		       String sPriceString = request.getParameter("s_price"); // 상품 가격을 문자열로 가져옵니다.
		       BigDecimal sPrice = new BigDecimal(sPriceString); // 문자열을 BigDecimal로 변환합니다.
		       shop.setSPrice(sPrice); // sPrice 값을 설정합니다.

		       String sAmountString = request.getParameter("s_amount"); // 상품 수량을 문자열로 가져옵니다.
		       int sAmount = Integer.parseInt(sAmountString); // 문자열을 int로 변환합니다.
		       shop.setSAmount(sAmount); // sAmount 값을 설정합니다.

		       shopService.updateShop(shop);
		       return "redirect:shopBoardList";
		   }
	   @GetMapping("/deleteShop")
	   public String deleteShop(Authentication authentication,@ModelAttribute("member") Member member, Shop shop, ShopReply shopReply) throws Exception{
			// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
		   shopReplyService.eraseReplyBySSeq(shop.getSSeq());
		   shopService.deleteShop(shop);
		   
		   return "forward:shopBoardList";
	   }  

	   @PostMapping("/addCart")
	      public String addCart(@ModelAttribute("cart") Cart cart, @ModelAttribute("shop") Shop shop, @ModelAttribute("member") Member member, Model model, HttpServletRequest request) throws Exception{
	         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	         if (authentication instanceof AnonymousAuthenticationToken) {
	             return "redirect:login";
	         }
	
	         String userId = authentication.getName(); // 현재 로그인한 사용자의 이름을 가져옵니다.
	         
	          shopService.shopBoardView(shop);
	          cart.setMemberId(userId); // memberId 값을 설정합니다.
	          
	          String sSeqParam = request.getParameter("s_seq"); // 상품 sSeq을 가져옵니다.
	          int sSeq = Integer.parseInt(sSeqParam); // 문자열을 int로 변환합니다.
	          cart.setSSeq(sSeq); // sSeq 값을 설정합니다.
	          
	          String sName = request.getParameter("s_name"); // 상품 이름을 가져옵니다.
	          cart.setSName(sName); // sName 값을 설정합니다.
	
	          String sPriceString = request.getParameter("s_price"); // 상품 가격을 문자열로 가져옵니다.
	          BigDecimal sPrice = new BigDecimal(sPriceString); // 문자열을 BigDecimal로 변환합니다.
	          cart.setSPrice(sPrice); // sPrice 값을 설정합니다.
	          
	          String sAmountString = request.getParameter("s_amount"); // 상품 수량을 문자열로 가져옵니다.
	          int sAmount = Integer.parseInt(sAmountString); // 문자열을 int로 변환합니다.
	          cart.setSAmount(sAmount); // sAmount 값을 설정합니다.
	          
	          String sUploadThumb = request.getParameter("s_upload_thumb");
	          cart.setSUploadThumb(sUploadThumb);
	          
	          boolean isAlreadyInCart = shopService.isProductInCart( userId,sSeq);
	          if (isAlreadyInCart) {
	              model.addAttribute("message", "이미 담긴 제품입니다.");
	              return "redirect:shop/shopBoardView?sSeq=" + sSeq + "&message=" + URLEncoder.encode("이미 담긴 제품입니다.", "UTF-8");
	          }else {
	             model.addAttribute("message", "장바구니에 담았습니다.");
	             shopService.addCart(cart);
	             return "redirect:shop/shopBoardView?sSeq=" + sSeq + "&message=" + URLEncoder.encode("장바구니에 담았습니다.", "UTF-8");
	          }
	
	      }

	   @RequestMapping("/cartList")
	   public String cartList(@ModelAttribute("member") Member member,
	      @RequestParam(value = "searchType", defaultValue = "") String searchType,
	      @RequestParam(value = "searchWord", defaultValue = "") String searchWord,
	      @RequestParam(value = "sName", defaultValue = "") String sName,
	      Model model) throws Exception {

	      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	      if (authentication instanceof AnonymousAuthenticationToken) {
	          return "redirect:login";
	      }

	      String userId = authentication.getName(); // 현재 로그인한 사용자의 이름을 가져옵니다.

	      List<Cart> cartList = shopService.cartList(searchWord, searchType, userId, sName)
	           .stream()
	           .filter(cart -> cart.getMemberId().equals(userId))
	           .collect(Collectors.toList());
	      BigDecimal totalPrice = shopService.calculateTotalPrice(userId);
	      model.addAttribute("totalPrice", totalPrice);	    
	      model.addAttribute("list", cartList);
	      return "shop/cartList";
	   }

	   @GetMapping("/deleteCart")
	   public String deleteCart(Authentication authentication,@ModelAttribute("member") Member member,
	       Shop shop,
	       Cart cart,
	        int sSeq,
	        int cartNo
	   ) throws Exception {
			// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
	       
	           shopService.deleteCart(sSeq, cartNo);
//	           System.out.println("시팔");
 
	       return "forward:/cartList";
	   } 
	   @GetMapping("/deleteSelectedCart")
	   public String deleteSelectedCart(Authentication authentication,@ModelAttribute("member") Member member,
	       @RequestParam(value = "selectedIds", defaultValue = "") String selectedIds
	   ) throws Exception {
			// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
	       
	       List<Integer> cartIds = Arrays.stream(selectedIds.split(","))
	               .map(Integer::parseInt)
	               .collect(Collectors.toList());

	       shopService.deleteSelectedCart(cartIds);

	       return "redirect:/cartList";
	   }
	   @GetMapping("/placeOrder")
	   public String getPlaceOrder(Authentication authentication,
	                               @ModelAttribute("member") Member memberParam,
	                               @RequestParam(value = "searchType", defaultValue = "") String searchType,
	                               @RequestParam(value = "searchWord", defaultValue = "") String searchWord,
	                               @RequestParam(value = "sName", defaultValue = "") String sName,
	                               Model model) {
	       // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	       // 사용자의 아이디를 가져옵니다.
	       String userId = userPrincipal.getUsername();

	       if (userId == null) {
	           return "redirect:/login";
	       }

	       Member member = memberService.getMemberById(userId);

	       List<Cart> cartList = shopService.cartList(searchWord, searchType, member.getId(), sName)
	               .stream()
	               .filter(cart -> cart.getMemberId().equals(member.getId()))
	               .collect(Collectors.toList());

	       BigDecimal totalPrice = shopService.calculateTotalPrice(member.getId());
	       member.setPoint(member.getPoint()); // memberId 값을 설정합니다.
			 for (Cart cart : cartList) {
			        Shop product = shopService.shopBoardViewBysSeq(cart.getSSeq());
			        model.addAttribute("product_" + cart.getSSeq(), product);
			    }
	       model.addAttribute("totalPrice", totalPrice);
	       model.addAttribute("list", cartList);
	       model.addAttribute("member",member);
	       return "shop/placeOrder";
	   }
  
	   @PostMapping("/placeOrder")
	   public String placeOrder(Authentication authentication,
			   					@ModelAttribute("member") Member member,
	                            @ModelAttribute("shop") Shop shop,
	                            @ModelAttribute("order") ShopOrder order,
	                            @RequestParam(value = "selectedIds", defaultValue = "") String selectedIds,
	                            HttpServletRequest request) throws Exception {
			// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 

	       order.setMemberId(member.getId()); // memberId 값을 설정합니다.
	       
	      

	       String totalPriceString = request.getParameter("total_price"); // 상품 가격을 문자열로 가져옵니다.
	       BigDecimal totalPrice = new BigDecimal(totalPriceString); // 문자열을 BigDecimal로 변환합니다.
	       order.setTotalPrice(totalPrice); // sPrice 값을 설정합니다.
	       String orderRec = request.getParameter("order_rec");
	       order.setOrderRec(orderRec);
	       String userAddr1 = request.getParameter("user_addr1");
	       order.setUserAddr1(userAddr1);
	       String userAddr2 = request.getParameter("user_addr2");
	       order.setUserAddr2(userAddr2);
	       String userAddr3 = request.getParameter("user_addr3");
	       order.setUserAddr3(userAddr3);
	       String phoneNum = request.getParameter("phone_num");
	       order.setPhoneNum(phoneNum);
	     
	    		   order.setDelivery("배송준비");
	       
	    		   String changePoint = request.getParameter("point"); // 결제 시 멤버 포인트컬럼 변경
	    	          BigDecimal point = new BigDecimal(changePoint);
	    	          member.setPoint(point);

	    	          String changeSAmount = request.getParameter("s_amount"); // 결제 시 s_board 재고 컬럼 변경
	    		       int sAmount = Integer.parseInt(changeSAmount); // 문자열을 int로 변환합니다.
	    		       shop.setSAmount(sAmount); // sAmount 값을 설정합니다.
	    		       
	    	          
	       shopService.orderInfo(order);
	       shopService.updatePoint(member.getPoint(), member.getId());
	       
	      

	       List<Cart> cartList = new ArrayList<>();
	       List<Cart> cartItems = shopService.getCartSSeqAndSAmount(member.getId()); // 해당 메소드명은 예시입니다. 실제로 상품 정보와 수량을 가져오는 메소드를 호출해야 합니다.

	       for (Cart cartItem : cartItems) {
	           Cart cart = new Cart();
	           cart.setSSeq(cartItem.getSSeq()); // 상품 정보 설정
	           cart.setSName(cartItem.getSName());
	           cart.setSPrice(cartItem.getSPrice());
	           cart.setSUploadThumb(cartItem.getSUploadThumb());
	           cart.setSAmount(cartItem.getSAmount()); // 상품 수량 설정
	    
	           cartList.add(cart);
	       }

	       for (Cart cartItem : cartList) {
	           ShopOrderDetail orderDetail = new ShopOrderDetail();
	           orderDetail.setOrderNo(order.getOrderNo()); // 주문 정보의 주문 번호를 설정
	           orderDetail.setSSeq(cartItem.getSSeq()); // 상품 정보 설정
	           orderDetail.setSName(cartItem.getSName());
	           orderDetail.setSPrice(cartItem.getSPrice());
	           orderDetail.setSUploadThumb(cartItem.getSUploadThumb());
	           orderDetail.setSAmount(cartItem.getSAmount()); // 상품 수량 설정
	           orderDetail.setOrderStatus("주문완료");
	           shopService.orderInfoDetails(orderDetail); // 주문 상세 정보를 저장
	           
	           // 재고 업데이트
	           int updatedSAmount = cartItem.getSAmount(); // 장바구니에 담긴 상품 수량
	           Shop product = shopService.shopBoardViewBysSeq(cartItem.getSSeq()); // 상품 정보 조회
	           if (product != null) {
	               int currentSAmount = product.getSAmount(); // 현재 상품의 재고
	               int remainingSAmount = currentSAmount - updatedSAmount; // 업데이트 후 남은 재고
	               shopService.updateSAmount(remainingSAmount, cartItem.getSSeq()); // 재고 업데이트
	           }
	       }
	       shopService.cartAllDelete(member.getId());
	       return "redirect:orderList";
	   }

	// 주문 목록
	   @RequestMapping("/orderList")
	   public String getOrderList(Authentication authentication,HttpSession session, ShopOrder order, Model model, @ModelAttribute("member") Member member) throws Exception {
		   
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
		   List<ShopOrder> orderList = shopService.orderList(userId);
		   model.addAttribute("orderList", orderList);
		   return "shop/orderList";
	   }
	   
	   @RequestMapping("/orderView")
	   public String getOrderView(Authentication authentication, HttpSession session, @ModelAttribute("shopOrder") ShopOrder order, ShopOrderDetail orderDetail, Model model, @ModelAttribute("member") Member member, @RequestParam("no") Integer orderNo) throws Exception {
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
					UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
					// 사용자의 아이디를 가져옵니다.
					String userId = userPrincipal.getUsername();
					
					if(userId == null) {
						return "redirect:/login";
					} 
	       order.setMemberId(userId);
		   order.setOrderNo(orderNo);
	       orderDetail.setOrderNo(orderNo);
	       
	       List<ShopOrder> orderInfo = shopService.getOrderInfo(orderNo); // 주문 정보 가져오기
		    model.addAttribute("orderInfo", orderInfo);
	       List<ShopOrderDetail> orderView = shopService.orderView(orderNo);
	       model.addAttribute("orderView", orderView);
	       return "shop/orderView";
	   }
	   
	   @PostMapping("/deleteOrder")
	   public String deleteOrder(Authentication authentication,Model model, HttpServletRequest request, @ModelAttribute("member") Member member, ShopOrderDetail orderDetail) throws Exception {
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			// 사용자의 아이디를 가져옵니다.
			String userId = userPrincipal.getUsername();
			
			if(userId == null) {
				return "redirect:/login";
			} 
	       
	       String orderNoString = request.getParameter("orderNo");
	       Integer orderNo = Integer.valueOf(orderNoString);
	       
	       String orderDetailNoString = request.getParameter("orderDetailNo");
	       Integer orderDetailNo = Integer.valueOf(orderDetailNoString);
	       
	       String orderStatus = request.getParameter("orderStatus");
	       orderDetail.setOrderStatus(orderStatus);
	       shopService.deleteOrder(orderNo, orderDetailNo, orderStatus);
	       
	       return "redirect:orderList";
	   }


}




