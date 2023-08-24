package com.lec.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.lec.domain.Community;
import com.lec.domain.Likes;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.domain.UserPrincipal;
import com.lec.service.CommunityReplyService;
import com.lec.service.CommunityService;
import com.lec.service.MemberService;

import lombok.RequiredArgsConstructor;


@Controller
@SessionAttributes("member")
@RequiredArgsConstructor
public class CommunityController {
   
   @Autowired
   private final CommunityService communityService;
   
   @Autowired
   private final MemberService memberService;
   
   @Autowired
   private final CommunityReplyService communityReplyService;
   
   public PagingInfo pagingInfo = new PagingInfo();
   
   @Value("${path.upload.com}")
   public String uploadFolder;

   @ModelAttribute("member")
   public Member setMember() {
      return new Member();
   }
   
   @GetMapping("/getCommunityList")
   public String getCommunityList(Authentication authentication,@ModelAttribute("member") Member member, Model model, 
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
      
      // 신고 여부에 따라 'reported' 필드 설정
       for (Community comm : communityList) {
           comm.setReported(comm.getReported_c_seq() != null);
       }
      
      model.addAttribute("boardList", communityList);
      model.addAttribute("memberId", userId);
      model.addAttribute("keyword", keyword);
      return "community/getCommunityList";
   }

   @GetMapping("/getCommunity")
   public String getCommunity(Authentication authentication,
         @ModelAttribute("member") Member member, Model model, 
         @RequestParam Integer c_seq, @RequestParam(required=false) String memberId,
         Community community) {

      
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 
      Community findCommunity = communityService.findCommunityById(c_seq);
      communityService.updateCount(community);
      List<Likes> likes = findCommunity.getLikes();
      boolean isLiked = false;
      for(Likes like:likes) {
         if(like.getMember().getId().equals(userId)) {
            isLiked = true;
            break;
         }
      }
      
      
      model.addAttribute("isLiked", isLiked);
      model.addAttribute("community", findCommunity);
      model.addAttribute("memberId", userId);
       return "community/getCommunity";
   }
   

   @GetMapping("/insertCommunity")
   public String insertCommunityForm(Authentication authentication,@ModelAttribute("member") Member member) {
      
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 
      return "community/insertCommunity";
   }
   
   @PostMapping("/insertCommunity")
   public String insertCommunity(Authentication authentication,@ModelAttribute("member") Member member, @ModelAttribute("c_board") Community community) throws IOException {

       // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
       
       if(userId == null) {
           return "redirect:/login";
       } 

       Member existingMember = memberService.findMemberById(userId); // 수정된 부분
             
       // 다중 파일 업로드
       List<MultipartFile> uploadFiles = community.getC_uploadFiles();
       if (uploadFiles != null && !uploadFiles.isEmpty()) {
           StringBuilder fileNames = new StringBuilder();
           for (MultipartFile file : uploadFiles) {
               String fileName = file.getOriginalFilename();
               file.transferTo(new File(uploadFolder + fileName));
               if (fileNames.length() > 0) {
                   fileNames.append(",");
               }
               fileNames.append(fileName);
           }
           community.setC_upload(fileNames.toString());
       }

       // c_like 필드에 기본값 설정
       community.setC_like(0);
       
       community.setMember(existingMember);

       communityService.insertCommunity(community);
       return "redirect:/getCommunityList";       
   }
   
   @GetMapping("/updateCommunityView")
   public String updateCommunityView(Authentication authentication,@ModelAttribute("member") Member member, @RequestParam("c_seq") Integer c_seq, Model model) {
      
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 

      
      Community community = communityService.findCommunityById(c_seq);
      model.addAttribute("community", community);
      return "community/updateCommunityView";
   }
   
   @PostMapping("/updateCommunity")
   public String updateCommunity(Authentication authentication,@ModelAttribute("member") Member member, @ModelAttribute("community") Community updateCommunity,
         Model model,
         @RequestParam("existingUploadFiles") String existingUploadFiles) throws Exception, IOException {
      
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 

       // 기존 이미지 파일명 리스트 가져옴
       List<String> existingList = new ArrayList<>(Arrays.asList(existingUploadFiles.split(",")));
             
      Community existingCommunity = communityService.findCommunityById(updateCommunity.getC_seq());
      
      existingCommunity.setC_content(updateCommunity.getC_content());
      
      // 새로운 이미지를 처리합니다.
        List<MultipartFile> uploadFiles = updateCommunity.getC_uploadFiles();
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
       
       // 기존 이미지를 업데이트
       String finalSUploads = existingList.isEmpty() ? "" : String.join(",", existingList);
       if(!finalSUploads.isEmpty() && finalSUploads.charAt(0) == ',') {
          finalSUploads = finalSUploads.substring(1);
       }
       updateCommunity.setC_upload(finalSUploads);
       
      communityService.updateCommunity(existingCommunity);
      return "redirect:getCommunityList";
   }
   
   @GetMapping("/deleteCommunity")
   public String deleteCommunity(Authentication authentication,@ModelAttribute("member") Member member, Community c_board, Model model) {
      
      // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
       // 사용자의 아이디를 가져옵니다.
       String userId = userPrincipal.getUsername();
      
       if(userId == null) {
          return "redirect:/login";
       } 
      communityService.deleteCommunity(c_board);
      return "forward:getCommunityList";
   }   
   
   @GetMapping("/deleteReportedCommunity/{c_seq}")
   public String deleteReportedCommunity(@PathVariable Integer c_seq, Model model) {
       Community reportedCommunity = communityService.findCommunityById(c_seq);
       
       if (reportedCommunity == null) {
           return "redirect:/getCommunityList";
       }

       communityService.deleteCommunity(reportedCommunity);
       return "redirect:/getCommunityList";
   }
   
   
   // 좋아요 매핑
   @PostMapping("/{c_seq}/like")
   public ResponseEntity<String> addLikeToCommunity(@PathVariable Integer c_seq, @RequestParam String memberId) {
       communityService.addLikeToCommunity(c_seq, memberId);
       return ResponseEntity.ok("Success");
   }
   
   @DeleteMapping("/{c_seq}/like")
   public ResponseEntity<String> removeLikeFromCommunity(@PathVariable Integer c_seq, @RequestParam String memberId) {
       communityService.removeLikeFromCommunity(c_seq, memberId);
       return ResponseEntity.ok("Success");
   }
   
   @GetMapping("/{c_seq}/like")
   public String getLikeFromCommunity(@PathVariable Integer c_seq, Model model) {
       int likeCount = communityService.getLikeCountForCommunity(c_seq);
       model.addAttribute("likeCount", likeCount);
       return "redirect:/getCommunityList";
   }
   
   @PostMapping("/uploadImage")
   @ResponseBody
   public List<String> uploadImage(@RequestParam("c_uploadFiles") List<MultipartFile> uploadFiles) throws IOException {
       List<String> uploadedImages = new ArrayList<>();

       for (MultipartFile file : uploadFiles) {
           if (!file.isEmpty() && StringUtils.hasText(file.getOriginalFilename())) {
               String fileName = file.getOriginalFilename();
               file.transferTo(new File(uploadFolder + fileName));
               uploadedImages.add(fileName);
           }
       }

       return uploadedImages;
   }
   
   @GetMapping("/searchCommunity")
   public ResponseEntity<String> searchCommunity(@RequestParam String keyword) {
       List<Community> boardList = communityService.searchCommunityByKeyword(keyword);

       // 검색 결과를 HTML 형태로 가공하여 반환합니다.
       StringBuilder htmlBuilder = new StringBuilder();
       for (Community community : boardList) {
           htmlBuilder.append("<tr>");
           // 게시글 정보를 테이블의 각 열에 맞게 추가합니다. (테이블의 컬럼에 맞게 변경해주시면 됩니다.)
           htmlBuilder.append("<td>").append(community.getC_seq()).append("</td>");
           htmlBuilder.append("<td>").append(community.getC_content()).append("</td>");
           htmlBuilder.append("<td>").append(community.getC_date()).append("</td>");
           // ... 이하 생략 (추가 필요)
           htmlBuilder.append("</tr>");
       }

       return ResponseEntity.ok(htmlBuilder.toString());
   }

   @GetMapping("/getLikedCommunities")
   public String getLikedCommunities(Authentication authentication,@ModelAttribute("member") Member member, Model model) {
   // 로그인 사용자의 정보를 Authentication 객체에서 가져옵니다.
          UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
          // 사용자의 아이디를 가져옵니다.
          String userId = userPrincipal.getUsername();
         
          if(userId == null) {
             return "redirect:/login";
          } 

       // 특정 회원의 좋아요한 게시물을 조회합니다.
       List<Community> likedCommunities = communityService.getLikedCommunitiesForMember(userId);
       // likes 필드를 기준으로 l_seq를 내림차순으로 정렬합니다.
       Collections.sort(likedCommunities, Comparator.comparing(c -> c.getLikes().get(0).getL_seq(), Comparator.reverseOrder()));

       model.addAttribute("likedCommunities", likedCommunities);

       return "community/getLikedCommunities";
   }
}