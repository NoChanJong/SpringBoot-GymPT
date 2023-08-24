package com.lec.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.Member;
import com.lec.domain.News;
import com.lec.domain.PagingInfo;
import com.lec.domain.UserPrincipal;
import com.lec.service.NewsService;

@Controller
@SessionAttributes({"member", "pagingInfo"})
public class NewsController {
   
   @Autowired
   private NewsService newsService;
   
   @Autowired
   Environment environment;
   
   @Value("${path.upload.news}")
   public String uploadFolder;
   
   
   public PagingInfo pagingInfo = new PagingInfo();
   
   @ModelAttribute("member")
   public Member setMember() {
      return new Member();
   }

   @RequestMapping("/getNewsList")
   public String getNewsList(Model model,
                             @RequestParam(defaultValue = "0") int curPage,
                             @RequestParam(defaultValue = "10") int rowSizePerPage,
                             @RequestParam(defaultValue = "n_title") String searchType,
                             @RequestParam(defaultValue = "") String searchWord,
                             Authentication authentication) {

       Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("n_seq").descending());
       Page<News> pagedResult = newsService.getNewsList(pageable, searchType, searchWord);

       int totalRowCount = pagedResult.getNumberOfElements();
       int totalPageCount = pagedResult.getTotalPages();
       int pageSize = pagingInfo.getPageSize();
       int startPage = curPage / pageSize * pageSize + 1;
       int endPage = startPage + pageSize - 1;
       endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;

       boolean isAdmin = false;
       if (authentication != null && authentication.getAuthorities() != null) {
           isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
       }
       model.addAttribute("isAdmin", isAdmin);

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
       return "news/getNewsList";
   }

   @GetMapping("/insertNews")
   public String insertNewsView(Authentication authentication, @ModelAttribute("member") Member member, Model model) {
       // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();

       if (userId == null) {
           return "redirect:/login";
       }

       boolean isAdmin = false;
       if (authentication != null && authentication.getAuthorities() != null) {
           isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
       }
       model.addAttribute("isAdmin", isAdmin);

       if (!isAdmin) {
           return "redirect:/getNewsList";
       }


       return "news/insertNews";
   }


   @PostMapping("/insertNews")
   public String insertNews(Authentication authentication,@ModelAttribute("member") Member member, News news, Model model,HttpServletRequest request, @RequestParam("n_upload") MultipartFile[] files) throws IOException {
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 

         boolean isAdmin = false;
          if (authentication != null && authentication.getAuthorities() != null) {
              isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
          }
          model.addAttribute("isAdmin", isAdmin);
          
       // 파일 업로드
       List<String> n_uploadFiles = new ArrayList<>();
       for (MultipartFile uploadFile : files) {
           if (!uploadFile.isEmpty()) {
               String n_upload = uploadFile.getOriginalFilename();
               uploadFile.transferTo(new File(uploadFolder + n_upload));
               n_uploadFiles.add(n_upload);
           }
       }
       news.setN_uploadFiles(n_uploadFiles);

       
       newsService.insertNews(news);
       return "redirect:getNews?n_seq=" + news.getN_seq();
   }




   @GetMapping("/getNews")
   public String getNews(Authentication authentication, @ModelAttribute("member") Member member, News news, Model model) {
       if (authentication != null) { // 로그인한 사용자인 경우
           UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
           String userId = userPrincipal.getUsername();
           if (userId == null) {
               return "redirect:/login";
           }

           boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
           model.addAttribute("isAdmin", isAdmin);
       } else { // 비로그인 사용자인 경우
           model.addAttribute("isAdmin", false); // 권한은 없음
       }

       newsService.updateNReadCount(news);
       model.addAttribute("news", newsService.getNews(news));
       return "news/getNews";
   }

   
   @GetMapping("/updateNews")
   public String updateNews(Authentication authentication,@ModelAttribute("member") Member member, News news, Model model) {
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 

         boolean isAdmin = false;
          if (authentication != null && authentication.getAuthorities() != null) {
              isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
          }
          model.addAttribute("isAdmin", isAdmin);
       model.addAttribute("news", newsService.getNews(news));
       return "news/updateNews";
   }
   
    // 뉴스 업데이트를 처리하는 핸들러
   @PostMapping("/updateNews")
   public String updateNewsform(Authentication authentication, @ModelAttribute("member") Member member, News news, Model model, @RequestParam("files") MultipartFile[] files) throws IOException {
       // 파일 업로드 처리
       if (files != null && files.length > 0) {
           List<String> n_uploadFiles = new ArrayList<>();
           for (MultipartFile uploadFile : files) {
               if (!uploadFile.isEmpty()) {
                   String n_upload = uploadFile.getOriginalFilename();
                   uploadFile.transferTo(new File(uploadFolder + n_upload));
                   n_uploadFiles.add(n_upload);
               }
           }
           news.setN_uploadFiles(n_uploadFiles);
       }

       // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();

       if (userId == null) {
           return "redirect:/login";
       }

       boolean isAdmin = false;
       if (authentication != null && authentication.getAuthorities() != null) {
           isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
       }
       model.addAttribute("isAdmin", isAdmin);

       // 뉴스 내용 업데이트
       newsService.updateNews(news);

       return "redirect:getNews?n_seq=" + news.getN_seq();
   }

   // 파일 업로드를 처리하는 핸들러
   @PostMapping("/uploadFiles")
   public String uploadFiles(@RequestParam("n_seq") Long n_seq, @RequestParam("files") MultipartFile[] files) throws IOException {
       // 뉴스 ID로 뉴스를 조회하여 기존 파일들을 가져옵니다.
       News existingNews = newsService.findNewsById(n_seq);
       List<String> existingUploadFiles = existingNews.getN_uploadFiles();

       // 새로 업로드된 파일들을 추가합니다.
       for (MultipartFile uploadFile : files) {
           if (!uploadFile.isEmpty()) {
               String n_upload = uploadFile.getOriginalFilename();
               uploadFile.transferTo(new File(uploadFolder + n_upload));
               existingUploadFiles.add(n_upload);
           }
       }

       // 업로드된 파일들을 뉴스 객체에 설정
       existingNews.setN_uploadFiles(existingUploadFiles);
       newsService.updateNews(existingNews);

       return "redirect:getNews?n_seq=" + n_seq;
   }


   
   @GetMapping("/deleteNews")
   public String deleteNews(Authentication authentication,@ModelAttribute("member") Member member, News news,Model model) {
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 

         boolean isAdmin = false;
          if (authentication != null && authentication.getAuthorities() != null) {
              isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
          }
          model.addAttribute("isAdmin", isAdmin);

       newsService.deletenews(news);

       return "redirect:getNewsList";
   }
    public int updateView(News news) {
        return newsService.updateNReadCount(news);
    }
}