package com.sp.app.controller;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.common.FileManager;
import com.sp.app.common.MyUtil;
import com.sp.app.domain.BoardManage;
import com.sp.app.domain.Community;
import com.sp.app.service.BoardManageService;
import com.sp.app.service.CommunityService;

@Controller
@RequestMapping("/community/*")
public class CommunityController {
	@Autowired
	private BoardManageService boardManageService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private MyUtil myUtil;
	
	@Autowired
	private FileManager fileManager;	
	
	@RequestMapping("{boardId}/list")
	public String list(@PathVariable String boardId,
			@RequestParam(value = "page", defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd,
			HttpServletRequest req,
			Model model) throws Exception {
		
		// 게시판 정보
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		// 게시글 리스트
		if(req.getMethod().equals("GET")) {
			kwd = URLDecoder.decode(kwd, "utf-8");
		}
		
		int size = 10;
		int total_page = 0;
		int dataCount = 0;

		// 전체 페이지 수
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("typeNo", boardManage.getTypeNo());
		map.put("schType", schType);
		map.put("kwd", kwd);
		
		dataCount = communityService.dataCount(map);
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

		List<Community> list = communityService.listCommunity(map);
		
		String cp = req.getContextPath();
		String listUrl = cp + "/community/" + boardId + "/list";
		if(kwd.length() != 0) {
			listUrl += "?schType=" + schType + "&kwd="
					+ URLEncoder.encode(kwd, "utf-8");
		}
		
		String paging = myUtil.paging(current_page, total_page, listUrl);

		model.addAttribute("boardManage", boardManage);
		
		model.addAttribute("list", list);
		model.addAttribute("page", current_page);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("size", size);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("schType", schType);
		model.addAttribute("kwd", kwd);
		
		return "community/list";
	}
	
	@GetMapping("{boardId}/write")
	public String writeForm(@PathVariable String boardId,
			Model model) throws Exception {
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		model.addAttribute("boardManage", boardManage);
		model.addAttribute("mode", "write");
		
		return "community/write";
	}
	
	@PostMapping("{boardId}/write")
	public String writeSubmit(@PathVariable String boardId,
			Community dto,
			HttpServletRequest req,
			HttpSession session) throws Exception {
		
		try {
			BoardManage boardManage = boardManageService.findById(boardId);
			if(boardManage == null) {
				return "community/error";
			}
			
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
			
			dto.setTableName(boardId);
			dto.setAttach(boardManage.getAttach());
			dto.setIpAddr(req.getRemoteAddr());
			
			communityService.insertCommunity(dto, "write", pathname);
			
		} catch (Exception e) {
		}
		
		return "redirect:/community/" + boardId + "/list";
	}
	
	@GetMapping("{boardId}/article/{num}")
	public String article(@PathVariable String boardId,
			@PathVariable long num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd,
			Model model) throws Exception {
		
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		kwd = URLDecoder.decode(kwd, "UTF-8");
		
		String query = "page=" + page;
		if(kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + 
					URLEncoder.encode(kwd, "UTF-8");
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("typeNo", boardManage.getTypeNo());
		map.put("num", num);
		
		communityService.updateHitCount(map);
		
		Community dto = communityService.findById(map);
		if(dto == null) {
			return "redirect:/community/" + boardId + "/list?" + query;
		}
		
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		
		// 이전, 다음
		map.put("schType", schType);
		map.put("kwd", kwd);
		if(boardManage.getTypeNo() == 1) {
			map.put("groupNum", dto.getGroupNum());
			map.put("orderNo", dto.getOrderNo());
		}
		
		Community prevDto = communityService.findByPrev(map);
		Community nextDto = communityService.findByNext(map);
		
		// 파일
		if(boardManage.getAttach() > 0) {
			List<Community> listFile = communityService.listCommunityFile(map);
			model.addAttribute("listFile", listFile);
		}

		// 답글
		if(boardManage.getTypeNo() == 2) {
			Community answerDto = communityService.findByQuestionAnswer(map);
			model.addAttribute("answerDto", answerDto);
		}
		
		model.addAttribute("boardManage", boardManage);
		
		model.addAttribute("dto", dto);
		model.addAttribute("prevDto", prevDto);
		model.addAttribute("nextDto", nextDto);
		model.addAttribute("page", page);
		model.addAttribute("query", query);
		
		return "community/article";
	}

	@GetMapping("{boardId}/update")
	public String updateForm(@PathVariable String boardId,
			@RequestParam long num,
			@RequestParam String page,
			Model model) throws Exception {
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("num", num);
		
		Community dto = communityService.findById(map);
		if(dto == null) {
			return "redirect:/community/" + boardId + "/list?page=" + page;
		}
		
		model.addAttribute("boardManage", boardManage);
		if(boardManage.getAttach() > 0) {
			List<Community> listFile = communityService.listCommunityFile(map);
			model.addAttribute("listFile", listFile);
		}
		
		model.addAttribute("dto", dto);
		model.addAttribute("page", page);
		model.addAttribute("mode", "update");
		
		return "community/write";
	}
	
	@PostMapping("{boardId}/update")
	public String updateSubmit(@PathVariable String boardId,
			Community dto,
			@RequestParam String page,
			HttpSession session) throws Exception {
		
		try {
			BoardManage boardManage = boardManageService.findById(boardId);
			if(boardManage == null) {
				return "community/error";
			}
			
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
			
			dto.setTableName(boardId);
			dto.setAttach(boardManage.getAttach());
			
			communityService.updateCommunity(dto, pathname);
			
		} catch (Exception e) {
		}
		
		return "redirect:/community/" + boardId + "/list?page=" + page;
	}
	
	@GetMapping("{boardId}/answer")
	public String answerForm(@PathVariable String boardId,
			@RequestParam long num,
			@RequestParam String page,
			Model model) throws Exception {

		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("num", num);
		
		Community dto = communityService.findById(map);
		if(dto == null) {
			return "redirect:/community/" + boardId + "/list?page=" + page;
		}
		dto.setContent("[" + dto.getSubject() + "] 에 대한 답변입니다.\n");

		model.addAttribute("boardManage", boardManage);
		
		model.addAttribute("dto", dto);
		model.addAttribute("page", page);
		model.addAttribute("mode", "answer");

		return "community/write";
	}

	@PostMapping("{boardId}/answer")
	public String answerSubmit(@PathVariable String boardId,
			Community dto,
			@RequestParam String page,
			HttpServletRequest req,
			HttpSession session) throws Exception {

		try {
			BoardManage boardManage = boardManageService.findById(boardId);
			if(boardManage == null) {
				return "community/error";
			}
			
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
			
			dto.setTableName(boardId);
			dto.setAttach(boardManage.getAttach());
			dto.setIpAddr(req.getRemoteAddr());
			
			communityService.insertCommunity(dto, "answer", pathname);
		} catch (Exception e) {
		}

		return "redirect:/community/" + boardId + "/list?page=" + page;
	}
	
	@PostMapping("{boardId}/questionAnswer")
	public String questionAnswerSubmit(@PathVariable String boardId,
			Community dto,
			@RequestParam String mode,
			@RequestParam String page) throws Exception {
		
		try {
			dto.setTableName(boardId);
			
			if(mode.equals("answer")) {
				communityService.insertQuestionAnswer(dto);
			} else {
				communityService.updateQuestionAnswer(dto);
			}
			
		} catch (Exception e) {
		}
		
		return "redirect:/community/" + boardId + "/list?page=" + page;
	}
	
	@GetMapping("{boardId}/delete")
	public String delete(@PathVariable String boardId,
			@RequestParam long num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd,
			HttpSession session) throws Exception {
		
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
		
		kwd = URLDecoder.decode(kwd, "UTF-8");
		String query = "page=" + page;
		if(kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + 
					URLEncoder.encode(kwd, "UTF-8");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("num", num);
		map.put("typeNo", boardManage.getTypeNo());
		map.put("attach", boardManage.getAttach());
		
		communityService.deleteCommunity(map, pathname);
		
		return "redirect:/community/" + boardId + "/list?" + query;
	}

	@GetMapping("{boardId}/deleteQuestionAnswer")
	public String deleteQuestionAnswer(@PathVariable String boardId,
			@RequestParam long num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd) throws Exception {
		
		BoardManage boardManage = boardManageService.findById(boardId);
		if(boardManage == null) {
			return "community/error";
		}
		
		kwd = URLDecoder.decode(kwd, "UTF-8");
		String query = "page=" + page;
		if(kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + 
					URLEncoder.encode(kwd, "UTF-8");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("num", num);
		
		communityService.deleteQuestionAnswer(map);
		
		return "redirect:/community/" + boardId + "/list?" + query;
	}
	
	@GetMapping("{boardId}/download/{fileNum}")
	public void download(@PathVariable String boardId,
			@PathVariable long fileNum,
			HttpServletResponse resp,
			HttpSession session) throws Exception {
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;

		boolean b = false;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableName", boardId);
		map.put("fileNum", fileNum);
		
		Community dto = communityService.findByFileId(map);
		if (dto != null) {
			String saveFilename = dto.getSaveFilename();
			String originalFilename = dto.getOriginalFilename();

			b = fileManager.doFileDownload(saveFilename, originalFilename, pathname, resp);
		}

		if (! b) {
			try {
				resp.setContentType("text/html; charset=utf-8");
				PrintWriter out = resp.getWriter();
				out.println("<script>alert('파일 다운로드가 불가능 합니다 !!!');history.back();</script>");
			} catch (Exception e) {
			}
		}
	}
	
	@PostMapping("{boardId}/deleteFile")
	@ResponseBody
	public Map<String, Object> deleteFile(
			@PathVariable String boardId,
			@RequestParam long fileNum,
			HttpSession session) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "community" + File.separator + boardId;
		
		String state = "true";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tableName", boardId);
			map.put("field", "fileNum");
			map.put("num", fileNum);
			
			communityService.deleteCommunityFile(map, pathname);
		} catch (Exception e) {
			state = "false";
		}
		
		model.put("state", state);
		
		return model;
	}
}
