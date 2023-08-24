package com.lec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


	@Controller
	public class AdminPageController {
	    @GetMapping("/adminpage")
	    public String showAdminPage(Model model) {
	        // 데이터 추가
	        model.addAttribute("message", "안녕하세요!");

	        return "admin/adminpage";
	    }
	}

