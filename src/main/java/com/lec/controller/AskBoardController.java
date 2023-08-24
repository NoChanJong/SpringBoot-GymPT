package com.lec.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import com.lec.domain.AskBoard;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.domain.UserPrincipal;
import com.lec.service.AskBoardService;

@Controller
@SessionAttributes({"member", "pagingInfo"})
public class AskBoardController {

	@Autowired
	private AskBoardService boardService;
	
	@Autowired
	Environment environment;	
	
	public PagingInfo pagingInfo = new PagingInfo();
	
	@Value("${path.upload}")
	public String uploadFolder;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}

	 // 작성자 ID를 가리는 메서드
		private String maskWriterId(String writerId) {
	        int length = writerId.length();
	        int halfLength = length / 2;

	        StringBuilder maskedWriterId = new StringBuilder(writerId.substring(0, halfLength));
	        for (int i = 0; i < length - halfLength; i++) {
	            maskedWriterId.append("*");
	        }

	        return maskedWriterId.toString();
	    }
	@RequestMapping("/getBoardList")
	public String getBoardList(Model model,
	        @RequestParam(defaultValue = "0") int curPage,
	        @RequestParam(defaultValue = "10") int rowSizePerPage,
	        @RequestParam(defaultValue = "title") String searchType,
	        @RequestParam(defaultValue = "") String searchWord) {

	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("seq").descending());
	    Page<AskBoard> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);

	    int totalRowCount = pagedResult.getNumberOfElements();
	    int totalPageCount = pagedResult.getTotalPages();
	    int pageSize = pagingInfo.getPageSize();
	    int startPage = curPage / pageSize * pageSize + 1;
	    int endPage = startPage + pageSize - 1;
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

	    List<AskBoard> boardList = pagedResult.getContent();
	    for (AskBoard board : boardList) {
	        AskBoard updatedBoard = boardService.getBoard(board);
	        
	        // 작성자 ID 업데이트
	        String writerId = updatedBoard.getWriter();
	        String maskedWriterId = maskWriterId(writerId);  // maskWriterId() 메서드는 아래에 정의
	        updatedBoard.setWriter(maskedWriterId);
	    }

	    model.addAttribute("boardList", boardList);


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

	    return "board/getBoardList";
	}


	@GetMapping("/insertBoard")
	public String insertBoardView(HttpSession session, Model model) {
	    Member member = (Member) session.getAttribute("member");
	    if (member == null) {
	        return "redirect:login";
	    }
	    
	    AskBoard board = new AskBoard();
	    board.setWriter(member.getId());
	    
	    model.addAttribute("board", board);
	    return "board/insertBoard";
	}


	@PostMapping("/insertBoard")
	public String insertBoard(HttpSession session, AskBoard board, @RequestParam("category") String category) throws IOException {
	    Member member = (Member) session.getAttribute("member");

	    if (member == null) {
	        return "redirect:/login";
	    }

	    // Member 객체를 생성한 후에 세션에서 가져온 값으로 필요한 값을 복사합니다.
	    Member newMember = new Member();
	    newMember.setId(member.getId());
	    newMember.setRole(member.getRole());
	     	
	    // 파일 업로드 처리
	    MultipartFile uploadFile = board.getUploadFile();
	    if (!uploadFile.isEmpty()) {
	        String fileName = uploadFile.getOriginalFilename();
	        uploadFile.transferTo(new File(uploadFolder + fileName));
	        board.setFileName(uploadFile.getOriginalFilename());
	    }

	    board.setWriter(newMember.getId());

	    boardService.insertBoard(board);
	    return "redirect:getBoardList";
	}




	@GetMapping("/getBoard")
	public String getBoard(@ModelAttribute("member") Member member, AskBoard board, Model model, HttpServletRequest request) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    boolean isAuthenticated = authentication.isAuthenticated();

	    if (!isAuthenticated) {
	        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
	        return "redirect:/login";
	    }

	    boardService.updateReadCount(board);
	    AskBoard updatedBoard = boardService.getBoard(board);
	    model.addAttribute("board", updatedBoard);
	    
	    // 사용자의 역할(role) 정보를 추가
	    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
	        model.addAttribute("isAdmin", true);
	    } else {
	        model.addAttribute("isAdmin", false);
	    }
	    
	    return "board/getBoard";
	}


	@PostMapping("/updateBoard")
	public String updateBoard(Authentication authentication, @ModelAttribute("board") AskBoard board) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return "redirect:login";
	    }

	    String currentUsername = authentication.getName();
	    AskBoard existingBoard = boardService.getBoard(board);

	    if (!existingBoard.getWriter().equals(currentUsername) && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
	        return "error/errorPage";
	    }

	    // 관리자가 아닌 경우에만 replyStatus 값을 그대로 유지합니다.
	    if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
	        board.setReplyStatus(existingBoard.getReplyStatus());
	    } else {
	        // 관리자가 수정하는 경우에는 replyStatus를 "답변완료"로 설정합니다.
	        board.setReplyStatus("답변완료");
	    }

	    boardService.updateBoard(board);
	    return "forward:getBoardList";
	}



   @GetMapping("/deleteBoard")
   public String deleteBoard(Authentication authentication,@ModelAttribute("member") Member member, AskBoard board) {
      
        // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // 사용자의 아이디를 가져옵니다.
        String userId = userPrincipal.getUsername();
       
        if(userId == null) {
           return "redirect:/login";
        } 

      boardService.deleteBoard(board);
      return "forward:getBoardList";
   }
	
	
	@PostMapping("/adminReply")
	public String adminReply(@ModelAttribute("member") Member member, AskBoard board, @RequestParam("reply") String reply) {
	    if (member == null) {
	        return "redirect:/login";
	    }

	    AskBoard existingBoard = boardService.getBoard(board); // 기존에 저장되어 있는 게시글 가져오기
	    if (!existingBoard.getWriter().equals(member.getId()) && !member.getRole().equals("ADMIN")) {
	        // 게시글 작성자와 현재 로그인한 사용자를 비교하고, 사용자가 관리자가 아닐 경우 에러 페이지로 리다이렉트
	        return "error/errorPage"; 
	    }

	    boardService.adminReply(board, reply); // BoardService를 사용하여 답변 처리
	    return "redirect:/getBoardList";
	}



	
	
	@RequestMapping("/download")
	public void download(HttpServletRequest req, HttpServletResponse res) throws Exception { 	
		req.setCharacterEncoding("utf-8");
		String fileName = req.getParameter("fn");
		
		String fromPath = uploadFolder + fileName;
		String toPath = uploadFolder + fileName;
	
		byte[] b = new byte[4096];
		File f = new File(toPath);
		FileInputStream fis = new FileInputStream(fromPath);
		
		String sMimeType = req.getSession().getServletContext().getMimeType(fromPath); // mimetype = file type : pdf, exe, txt.... 
		if(sMimeType == null) sMimeType = "application/octet-stream";
		
		String sEncoding = new String(fileName.getBytes("utf-8"), "8859_1");
		String sEncoding1 = URLEncoder.encode(fileName, "utf-8");
		
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
	
    public int updateView(AskBoard board) {
        return boardService.updateReadCount(board);
    }    

    
    
}
