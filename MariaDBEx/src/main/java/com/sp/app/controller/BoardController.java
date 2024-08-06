package com.sp.app.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.common.MyUtil;
import com.sp.app.domain.Board;
import com.sp.app.service.BoardService;

// @Controller : Spring MVC 컨트롤러
/*
* - 의존관계 설정 방법
*   1. 타입에 의한 의존관계 자동 설정
*      @Autowired
*   2. 동일한 타입이 둘 이상인 경우
*      1) 방법 1
*         @Autowired
*         @Qualifier("bbs.boardService")
*      2) 방법 2
*         @Resource(name="bbs.boardService")
*           
*  - @RequestMapping
*     MultiActionController와 같이 한 개의 컨트롤러에서 다수의 요청을 처리.
*     @RequestMapping의 value 형태의 url이 들어오면 해당 method를 호출
*    
*  - @GetMapping("/uri")
*     @RequestMapping(value="/uri", method=RequestMethod.GET) 축약형
*     Sprinr 4.3 부터 지원
*     
*  - @PostMapping("/uri")
*     @RequestMapping(value="/uri", method=RequestMethod.POST) 축약형
*     Sprinr 4.3 부터 지원
*/

@Controller
@RequestMapping("/bbs/*")
public class BoardController {
	@Autowired
	private BoardService service;

	@Autowired
	private MyUtil myUtil;

	// method를 생략하면 GET, POST 모두를 처리
	@RequestMapping(value = "list")
	public String list(@RequestParam(value = "page", defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd,
			HttpServletRequest req,
			Model model) throws Exception {
		// 게시글 리스트
		
		int size = 10; // 한 화면에 보여주는 게시물 수
		int total_page = 0;
		int dataCount = 0;

		if (req.getMethod().equalsIgnoreCase("GET")) { // GET 방식인 경우
			kwd = URLDecoder.decode(kwd, "utf-8");
		}

		// 전체 페이지 수
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("schType", schType);
		map.put("kwd", kwd);

		dataCount = service.dataCount(map);
		if (dataCount != 0) {
			total_page = myUtil.pageCount(dataCount, size);
		}

		// 다른 사람이 자료를 삭제하여 전체 페이지수가 변화 된 경우
		if (total_page < current_page) {
			current_page = total_page;
		}

		// 리스트에 출력할 데이터를 가져오기
		int offset = (current_page - 1) * size;
		if(offset < 0) offset = 0;
		map.put("offset", offset);
		map.put("size", size);

		List<Board> list = service.listBoard(map);

		String query = "";
		String cp = req.getContextPath();
		String listUrl;
		String articleUrl;
		if (kwd.length() != 0) {
			query = "schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "utf-8");
		}

		listUrl = cp + "/bbs/list";
		articleUrl = cp + "/bbs/article?page=" + current_page;
		if (query.length() != 0) {
			listUrl = listUrl + "?" + query;
			articleUrl = articleUrl + "&" + query;
		}

		String paging = myUtil.paging(current_page, total_page, listUrl);

		model.addAttribute("list", list);
		model.addAttribute("articleUrl", articleUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("size", size);
		model.addAttribute("paging", paging);

		model.addAttribute("schType", schType);
		model.addAttribute("kwd", kwd);

		return "bbs/list";
	}

	@GetMapping("write")
	public String writeForm(Model model) throws Exception {
		// 게시글 등록 폼
		model.addAttribute("mode", "write");
		return "bbs/write";
	}

	@PostMapping("write")
	public String writeSubmit(Board dto, HttpServletRequest req) throws Exception {
		// 게시글 저장
		try {
			dto.setIpAddr(req.getRemoteAddr());
			service.insertBoard(dto);
		} catch (Exception e) {
		}

		return "redirect:/bbs/list";
	}

	@GetMapping("article")
	public String article(@RequestParam long num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd,
			Model model) throws Exception {
		// 게시글 보기
		kwd = URLDecoder.decode(kwd, "utf-8");

		String query = "page=" + page;
		if (kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
		}

		// 조회수 증가 및 해당 레코드 가져 오기
		service.updateHitCount(num);

		Board dto = service.findById(num);
		if (dto == null) {
			return "redirect:/bbs/list?" + query;
		}

		// 스타일로 처리하는 경우 : style="white-space:pre;"
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// 이전 글, 다음 글
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("schType", schType);
		map.put("kwd", kwd);
		map.put("num", num);

		Board prevDto = service.findByPrev(map);
		Board nextDto = service.findByNext(map);

		model.addAttribute("dto", dto);
		model.addAttribute("prevDto", prevDto);
		model.addAttribute("nextDto", nextDto);

		model.addAttribute("page", page);
		model.addAttribute("query", query);

		return "bbs/article";
	}

	@GetMapping("update")
	public String updateForm(@RequestParam long num,
			@RequestParam String page,
			Model model) throws Exception {
		// 수정 폼
		Board dto = service.findById(num);
		if (dto == null) {
			return "redirect:/bbs/list?page=" + page;
		}

		model.addAttribute("mode", "update");
		model.addAttribute("page", page);
		model.addAttribute("dto", dto);

		return "bbs/write";
	}

	@PostMapping("update")
	public String updateSubmit(Board dto,
			@RequestParam String page) throws Exception {
		// 수정 완료
		try {
			service.updateBoard(dto);
		} catch (Exception e) {
		}

		return "redirect:/bbs/list?page=" + page;
	}

	@RequestMapping(value = "delete")
	public String delete(@RequestParam long num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String schType,
			@RequestParam(defaultValue = "") String kwd) throws Exception {
		// 게시글 삭제
		kwd = URLDecoder.decode(kwd, "utf-8");
		String query = "page=" + page;
		if (kwd.length() != 0) {
			query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
		}

		try {
			service.deleteBoard(num);
		} catch (Exception e) {
		}

		return "redirect:/bbs/list?" + query;
	}
}
