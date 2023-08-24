package com.lec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lec.domain.Community;
import com.lec.domain.News;
import com.lec.domain.Shop;
import com.lec.service.CommunityService;
import com.lec.service.NewsService;
import com.lec.service.ShopService;

@Controller
public class IndexController {
   private final ShopService shopService;
   private final NewsService newsService;
   private final CommunityService communityService;

   @Autowired
   public IndexController(ShopService shopService, NewsService newsService, CommunityService communityService) {
      this.shopService = shopService;
      this.newsService = newsService;
      this.communityService = communityService;
      
   }

   @GetMapping("/index")
   public String showIndexPage(Model model) {
      String searchType = "all";
      String searchWord = "";
      String sContent = "";
      String sName = "";
      String sCate = "";
      String memberId = "";

      Page<Shop> shopPage = shopService.ShopBoardList(null, searchType, searchWord, sContent, sName, sCate, memberId);
      Pageable pageable = PageRequest.of(0, 10);
      Page<News> newsPage = newsService.getNewsList(pageable, searchType, searchWord);
      List<Community> communityList = communityService.getCommunityList(); // Community 데이터를 가져옴
     

      
      
      
      model.addAttribute("newsPage", newsPage);
      model.addAttribute("shops", shopPage);
      model.addAttribute("communityList", communityList); // Community 데이터를 뷰로 전달

      return "index";
   }

}



