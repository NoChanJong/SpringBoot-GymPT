package com.lec.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lec.domain.Community;
import com.lec.domain.Member;
import com.lec.domain.PagingInfo;
import com.lec.persistence.CommunityRepository;

@Controller
public class ReportController {

   @Autowired
   private CommunityRepository communityRepository;
   
   @Autowired
    private EntityManager entityManager;
   
   @PostMapping("/reportCommunity")
    public String reportCommunity(@RequestParam("c_seq") Integer cSeq, @RequestParam("report_reason") List<String> reportReason,
          Model model) {
        System.out.println("신고된 게시물 번호: " + cSeq);
        System.out.println("신고 사유: " + reportReason);
        
        Community community = communityRepository.findById(cSeq).orElse(null);
        if(community != null) {
           community.setReported(true);
           community.setProcessed(false);
           community.setReported_c_seq(cSeq);
           community.setReport_reason(String.join(", ", reportReason));
           
           communityRepository.save(community);
        }
        
        return "redirect:/getCommunityList"; // 신고 후 게시판 목록 페이지로 리다이렉트합니다.
    }
   
   @GetMapping("/reportedCommunityList")
    public String getReportedCommunityList(Model model, @ModelAttribute("member") Member member,
          @ModelAttribute("pagingInfo") PagingInfo pagingInfo) {
        // 신고된 게시물 목록을 조회하는 로직을 구현
        // reported_c_seq가 비어있지 않은 게시물을 조회
        String jpql = "SELECT c FROM Community c WHERE c.reported_c_seq IS NOT NULL ORDER BY c.reported_c_seq DESC";
        TypedQuery<Community> query = entityManager.createQuery(jpql, Community.class);
        
        // 전체 신고된 게시물 수를 가져오는 쿼리문을 만듭니다.
        String countJpql = "SELECT COUNT(c) FROM Community c WHERE c.reported_c_seq IS NOT NULL";
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        Long totalReportedCommunities = countQuery.getSingleResult();
        
        // 페이징 처리를 위한 정보를 가져옵니다.
        int rowSizePerPage = pagingInfo.getRowSizePerPage(); // 한 페이지에 보여줄 게시물 수
        int curPage = pagingInfo.getCurPage(); // 현재 페이지

        // 총 페이지 수를 계산합니다.
        int totalPage = (int) Math.ceil((double) totalReportedCommunities / rowSizePerPage);

        // 현재 페이지에 해당하는 신고된 게시물을 조회하기 위해 시작 인덱스를 계산합니다.
        int startIdx = (curPage - 1) * rowSizePerPage;
        query.setFirstResult(startIdx);
        query.setMaxResults(rowSizePerPage);
        
        List<Community> reportedCommunityList = query.getResultList();
        
        // 각 게시물의 신고 처리 상태를 확인하여, 뷰에 전달할 데이터를 추가
        for (Community community : reportedCommunityList) {
            boolean processed = community.isProcessed();
            model.addAttribute("community_" + community.getC_seq() + "_processed", processed);
        }
        
        model.addAttribute("reportedCommunityList", reportedCommunityList);
        model.addAttribute("totalReportedCommunities", totalReportedCommunities); // 총 신고된 게시물 수
        model.addAttribute("totalPage", totalPage); // 총 페이지 수
        model.addAttribute("curPage", curPage); // 현재 페이지
        return "community/reportedCommunityList"; // community 폴더의 신고된 게시물 목록 화면으로 이동합니다.
    }
   
   @PostMapping("/processReport")
   public String processReport(@RequestParam("c_seq") Integer c_seq,
                               @RequestParam("action") String action, Model model,
                               @ModelAttribute("member") Member member) {
       Community community = communityRepository.findById(c_seq).orElse(null);
       if (community != null) {
           if ("block".equals(action)) {
               community.setProcessed(true);
               communityRepository.save(community);
           } else if("cancel".equals(action)) {
              community.setReported(false);
              community.setProcessed(false);
              community.setReported_c_seq(null);
              community.setReport_reason(null);
           }
           
           communityRepository.save(community);
       }
       return "redirect:/reportedCommunityList";
   }
   
}