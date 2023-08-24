package com.lec.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.Community;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.domain.PointList;
import com.lec.domain.SessionMember;
import com.lec.domain.UserPrincipal;
import com.lec.service.CommunityService;
import com.lec.service.MemberService;

@Controller
@SessionAttributes({"pagingInfo"})
public class MemberController {

	@Value("${path.upload.profile}")
	private String uploadFolder;
	
	@Autowired
	private MemberService memberService;	
	
	@Autowired
	private CommunityService communityService ;	
	
	@Autowired
	HttpSession session;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}
	
	public PagingInfo pagingInfo = new PagingInfo();
	
	@RequestMapping("getMemberList")
	public String getMemberList(Model model,
			@RequestParam(defaultValue="0") int curPage,
			@RequestParam(defaultValue="6") int rowSizePerPage,
			@RequestParam(defaultValue="nickname") String searchType,
			@RequestParam(defaultValue="") String searchWord) {   			
		
		Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("id").ascending());
		Page<Member> pagedResult = memberService.selectMember(pageable, searchType, searchWord);
	
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
		return "member/getMemberList";
	}
	
	
	
	@RequestMapping("selectMember")
	public String selectMember(Model model,
			@RequestParam(defaultValue="0") int curPage,
			@RequestParam(defaultValue="1000") int rowSizePerPage,
			@RequestParam(defaultValue="nickname") String searchType,
			@RequestParam(defaultValue="") String searchWord) {   			
		
		Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("id").ascending());
		Page<Member> pagedResult = memberService.selectMember(pageable, searchType, searchWord);
		
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
		return "member/selectMember";
	}
	
	
		@GetMapping("/insertMember")
		public String insertMemberForm(Model model) {
		    model.addAttribute("member", new Member());
		    return "member/insertMember";
		}
		
		  @PostMapping("/insertMember")
		   public String insertMember(Member member) throws IOException {
		       if (member.getId() == null) {
		           return "redirect:login";
		       }
		       
		       // 파일 업로드
		       MultipartFile uploadFile = member.getUploadFile();
		       if (!uploadFile.isEmpty()) {
		           String fileName = uploadFile.getOriginalFilename();
		           uploadFile.transferTo(new File(uploadFolder + fileName));
		           member.setProfile(fileName);
		       } else {
		           // 파일이 업로드되지 않았을 경우 기본 이미지 파일을 설정
		          member.setProfile("default_profile.png");
		       }


		    member.setRole(member.getRole() != null ? "ADMIN" : "USER");
		    // 새로운 필드 값 설정
		    member.setPoint(BigDecimal.ZERO);
		    memberService.insertMember(member);

		    // 사용자 정보 세션에 저장
		    SessionMember sessionMember = new SessionMember(member);
		    session.setAttribute("member", sessionMember);

		    return "redirect:login";
		}

		  @GetMapping("deleteMember")
		  public String deleteMember(Authentication authentication, Member member, HttpServletRequest request) {
		      System.out.println("1........" + member.toString());

		      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
		      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		      // 사용자의 아이디를 가져옵니다.
		      String userId = userPrincipal.getUsername();

		      if (userId == null) {
		          return "redirect:/login";
		      }
		     
		      memberService.deleteMemberAndChatRooms(member);

		      // 로그아웃 처리
		      SecurityContextHolder.clearContext(); // 현재 인증 정보 제거
		      request.getSession().invalidate(); // 세션 무효화

		      return "redirect:/index?logout"; // 로그아웃 후 로그인 페이지로 리다이렉트
		  }

	  
	  @GetMapping("adminDeleteMember")
	  public String adminDeleteMember(Authentication authentication,Member member) {
		  
		  System.out.println("1........" + member.toString());
		  
		  // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
		  UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		  // 사용자의 아이디를 가져옵니다.
		  String userId = userPrincipal.getUsername();
		  if(userId == null) {
			  return "redirect:/login";
		  } 
		  memberService.deleteMemberAndChatRooms(member);
		  return "redirect:getMemberList";      
	  }

	@GetMapping("/updateMember")
	public String updateMember(Authentication authentication,Model model, HttpSession session) {
		
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	 	UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	 	// 사용자의 아이디를 가져옵니다.
	 	String userId = userPrincipal.getUsername();
		
	 	if(userId == null) {
	 		return "redirect:/login";
	 	} 
	    model.addAttribute("member", memberService.getMemberById(userId));
	    return "member/updateMember";
	}


	 @PostMapping("/updateMember")
	   public String updateMember(Authentication authentication,Member member, @RequestParam("uploadFile") MultipartFile file, HttpSession session) {
	       // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	       // 사용자의 아이디를 가져옵니다.
	       String userId = userPrincipal.getUsername();

	       if(userId == null) {
	           return "redirect:/login";
	       } 

	       // 프로필 이미지 업로드 처리
	       if (!file.isEmpty()) {
	           try {
	               String fileName = file.getOriginalFilename();
	               String savePath = uploadFolder + fileName;
	               file.transferTo(new File(savePath));
	               member.setProfile(fileName); // 새로운 파일명을 업데이트
	           } catch (IOException e) {
	               // 파일 업로드 실패시 처리할 로직
	               e.printStackTrace();
	           }
	       } else {
	           // 파일이 비어있으면 기존의 프로필 사진을 유지하기 위해 member 객체에서 프로필 파일명을 가져옴
	           Member existingMember = memberService.getMemberById(userId);
	           member.setProfile(existingMember.getProfile());
	       }

	       // 닉네임 업데이트 처리
	       memberService.updateMember(member);

	       // 세션에 저장된 UserPrincipal 객체 업데이트
	       userPrincipal.setNickname(member.getNickname());
	       userPrincipal.setProfile(member.getProfile());
	       userPrincipal.setWeight(member.getWeight());
	       userPrincipal.setHeight(member.getHeight());
	       session.setAttribute("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(authentication));

	       // 세션에 저장된 member 객체 업데이트 (생략 가능)
	       // session.setAttribute("member", updatedMember);

	       return "redirect:updateMember?id=" + userId;  
	   }

	
	@GetMapping("/existsById")
	public ResponseEntity<Boolean> existsById(@RequestParam String id){
		boolean exists = memberService.existsById(id);
		return ResponseEntity.ok().body(exists);
	}
	
	@GetMapping("/checkId")
	public ResponseEntity<Boolean> checkId(@RequestParam String id) {
	    boolean exists = memberService.existsById(id);
	    return ResponseEntity.ok().body(exists);
	}
	
	@GetMapping("/insertPoint")
	   public String getInsertPoint(Model model, Authentication authentication,  @ModelAttribute("member") Member memberParam) {
	      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
	      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	      // 사용자의 아이디를 가져옵니다.
	      String userId = userPrincipal.getUsername();
	      
	      if(userId == null) {
	         return "redirect:/login";
	      } 
	      Member member = memberService.getMemberById(userId);
	      member.setPoint(member.getPoint()); // memberId 값을 설정합니다.
	      model.addAttribute("member",member);
	      return "member/insertPoint";
	   }
	@PostMapping("/insertPoint")
	public String insertPoint(Authentication authentication,@ModelAttribute("member")Member member, PointList pointList, HttpServletRequest request) {
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
					UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
					// 사용자의 아이디를 가져옵니다.
					String userId = userPrincipal.getUsername();
		
					if(userId == null) {
						return "redirect:/login";
					} 
		   pointList.setMemberId(userId); // memberId 값을 설정합니다.
			
		   String memberPoint = request.getParameter("memberPoint"); // 상품 가격을 문자열로 가져옵니다.
		   BigDecimal point = new BigDecimal(memberPoint); // 문자열을 BigDecimal로 변환합니다.
		   pointList.setCurrentPoint(point); // point 값을 설정합니다.
		   
		   String insertPoint = request.getParameter("insertPoint"); // 상품 가격을 문자열로 가져옵니다.
		   BigDecimal updatePoint = new BigDecimal(insertPoint); // 문자열을 BigDecimal로 변환합니다.
		   pointList.setInsertPoint(updatePoint); // point 값을 설정합니다.
		   
//		   PointList pointList = new pointList();
		   pointList.setSendMoney("입금대기");

	       memberService.insertPoint(pointList);
	       return "member/paycheckPlease";
	}
	
	@PostMapping("/updatePoint")
	public String updatePoint(Member member) {
	    if (member.getId() == null) {
	        return "redirect:login";
	    }

	    memberService.updatePoint(member);

	    // Add logging to check if the method is called and the redirect is successful
	    System.out.println("Point update request for member: " + member.getId());

	    return "redirect:getMemberList";
	}
	
	@GetMapping("/getMember")
	public String getMember(Authentication authentication,@ModelAttribute("member") Member member, Model model, 
			Community community, @RequestParam(required = false) String keyword) {
		
		// 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		// 사용자의 아이디를 가져옵니다.
		String userId = userPrincipal.getUsername();
		
		
		if(userId == null) {
			return "redirect:/login";
		} 
		
		List<Community> communityList;
		
		if(keyword != null && !keyword.isEmpty()) {
			communityList = communityService.searchCommunityByKeyword(keyword);
		} else {
			// 키워드가 없는 경우 모든 게시글을 가져옴
			communityList = communityService.getCommunityList();
		}
		
		model.addAttribute("boardList", communityList);
		model.addAttribute("memberId", member.getId());
		model.addAttribute("keyword", keyword);
		model.addAttribute("member", memberService.getMember(member));
		return "member/getMember";
	}
}
