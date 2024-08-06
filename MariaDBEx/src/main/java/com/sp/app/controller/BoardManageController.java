package com.sp.app.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.common.MyUtil;
import com.sp.app.domain.BoardManage;
import com.sp.app.domain.BoardType;
import com.sp.app.service.BoardManageService;

@Controller
@RequestMapping("/boardManage/*")
public class BoardManageController {
	@Autowired
	private BoardManageService boardManageService;
	
	@Autowired
	private MyUtil myUtil;
	
	@GetMapping("main")
	public String main(@RequestParam(value = "page", defaultValue = "1") int current_page,
			HttpServletRequest req,
			Model model) throws Exception {
		
		int size = 10;
		int total_page = 0;
		int dataCount = 0;

		// 전체 페이지 수
		Map<String, Object> map = new HashMap<String, Object>();

		dataCount = boardManageService.dataCount(map);
		if (dataCount != 0) {
			total_page = myUtil.pageCount(dataCount, size);
		}
		
		if (total_page < current_page) {
			current_page = total_page;
		}

		int offset = (current_page - 1) * size;
		if(offset < 0) offset = 0;

		map.put("offset", offset);
		map.put("size", size);

		// 글 리스트
		List<BoardManage> list = boardManageService.listBoard(map);
		
		String cp = req.getContextPath();
		String listUrl = cp + "/boardManage/main";
		String paging = myUtil.paging(current_page, total_page, listUrl);

		model.addAttribute("list", list);
		model.addAttribute("page", current_page);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("size", size);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		
		model.addAttribute("BOARD_TYPES", BoardType.BOARD_TYPES);
		
		return "boardManage/list";
	}
	
	@PostMapping("create")
	public String createSubmit(BoardManage dto) {
		
		try {
			boardManageService.createBoard(dto);
		} catch (Exception e) {
		}
		
		return "redirect:/boardManage/main";
	}
	
	@GetMapping("drop")
	public String dropSubmit(@RequestParam String boardId,
			@RequestParam String page,
			HttpSession session) throws Exception {
		
		try {
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
			
			boardManageService.dropBoard(boardId, pathname);
		} catch (Exception e) {
		}
		
		return "redirect:/boardManage/main?page=" + page;
	}
	
}
