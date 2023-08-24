package com.lec.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.AskBoard;
import com.lec.domain.Inform;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.service.InformService;

@Controller
public class InformController {

    @Autowired
    private InformService informService;
    
    @Value("${path.upload.inform}")
    private String UPLOAD_DIR;

    
    public PagingInfo pagingInfo = new PagingInfo();
    
    @GetMapping("/getInformlist")
    public String getInformlist(Model model,
                                @RequestParam(defaultValue = "0") int curPage,
                                @RequestParam(defaultValue = "10") int rowSizePerPage,
                                @RequestParam(defaultValue = "iTitle") String searchType,
                                @RequestParam(defaultValue = "") String searchWord) {

        Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("iSeq").descending());
        Page<Inform> pagedResult = informService.getInformlist(pageable, searchType, searchWord);

        int totalRowCount = pagedResult.getNumberOfElements();
        int totalPageCount = pagedResult.getTotalPages();
        int pageSize = rowSizePerPage;
        int startPage = curPage / pageSize * pageSize + 1;
        int endPage = startPage + pageSize - 1;
        endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;

        List<Inform> informList = pagedResult.getContent();
        boolean isAdmin = false;

        for (Inform inform : informList) {
            Inform updateInform = informService.getInform(inform);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }

        pagingInfo.setCurPage(curPage);
        pagingInfo.setTotalRowCount(totalRowCount);
        pagingInfo.setTotalPageCount(totalPageCount);
        pagingInfo.setStartPage(startPage);
        pagingInfo.setEndPage(endPage);
        pagingInfo.setSearchType(searchType);
        pagingInfo.setSearchWord(searchWord);
        pagingInfo.setRowSizePerPage(rowSizePerPage);
        model.addAttribute("pagingInfo", pagingInfo);

        
        model.addAttribute("informlist", informList);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("pagedResult", pagedResult);
        model.addAttribute("cp", curPage);
        model.addAttribute("sp", startPage);
        model.addAttribute("ep", endPage);
        model.addAttribute("ps", pageSize);
        model.addAttribute("rp", rowSizePerPage);
        model.addAttribute("tp", totalPageCount);
        model.addAttribute("st", searchType);
        model.addAttribute("sw", searchWord);

        return "board/getInformlist";
    }


    
    @GetMapping("/getInform")
    public String getInform(@ModelAttribute("member") Member member, Inform inform, Model model) {
        Inform existingInform = informService.getInform(inform);
        model.addAttribute("inform", existingInform);

        // 현재 인증된 사용자의 역할을 확인하여 관리자인 경우 isAdmin 값을 true로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "board/getInform";
    }


    @GetMapping("/insertInform")
    public String insertInform(Model model) {
        Inform inform = new Inform();
        model.addAttribute("inform", inform);
        return "board/insertInform";
    }

   
    @PostMapping("/insertInform")
    public String insertInform(@ModelAttribute("member") Member member, Inform inform,
                               @RequestParam("iUpload") MultipartFile[] files) {
        inform.setIDate(LocalDateTime.now());
        inform.setMemberId("관리자");
        
        try {
            List<String> fileNames = new ArrayList<>(); // 파일 이름을 저장할 리스트 생성
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFilename = file.getOriginalFilename();
                    String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                    Path filePath = Paths.get(UPLOAD_DIR + uniqueFilename);
                    Files.write(filePath, file.getBytes());
                    
                    fileNames.add(uniqueFilename); // 변경된 파일 이름을 리스트에 추가
                }
            }

            
            inform.setFileNames(fileNames); // Inform 객체에 파일 이름 리스트 설정
            
            informService.insertInform(inform);
        } catch (IOException e) {
            e.printStackTrace();
            // 파일 업로드 실패 시 예외 처리 코드 작성
        }	
        return "redirect:/getInformlist";
    }



    @GetMapping("/updateInform")
    public String updateInformView(@RequestParam("informSeq") int informSeq, Model model) {
        Inform inform = new Inform();
        inform.setISeq(informSeq);
        Inform existingInform = informService.getInform(inform);

        // 관리자 역할을 가진 사용자만 수정 가능
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "error/accessDenied";
        }

        model.addAttribute("inform", existingInform);
        return "board/updateInform";
    }


    
    @PostMapping("/updateInform")
    public String updateInform(@ModelAttribute("inform") Inform updatedInform) {
        Inform existingInform = informService.getInform(updatedInform);
        existingInform.setITitle(updatedInform.getITitle()); // 제목 업데이트
        existingInform.setIContent(updatedInform.getIContent()); // 내용 업데이트
        informService.updateInform(existingInform);
        return "redirect:/getInformlist";
    }
    
    
    @GetMapping("/deleteInform")
    // 여기도 마찬가지 시큐리티로 권한 확인
    public String deleteInform(@RequestParam("informSeq") int informSeq) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/getInformlist";
        }

        Inform inform = new Inform();
        inform.setISeq(informSeq);
        informService.deleteInform(inform);
        return "redirect:/getInformlist";
    }

    
}
