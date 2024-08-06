package com.sp.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.app.common.FileManager;
import com.sp.app.domain.BoardManage;
import com.sp.app.domain.BoardType;
import com.sp.app.mapper.BoardManageMapper;

@Service
public class BoardManageServiceImpl implements BoardManageService {
	@Autowired
	private BoardManageMapper mapper;
	
	@Autowired
	private FileManager fileManage;
	
	@Override
	public void createBoard(BoardManage dto) throws Exception {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tableName", dto.getBoardId());
			
			mapper.insertBoardManage(dto);
			mapper.createBoardTable(map);
			
			if(dto.getAttach() > 0) {
				mapper.createBoardFileTable(map);
			}
			
			if(dto.getTypeNo() == 2) {
				mapper.createAnswerTable(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void dropBoard(String boardId, String pathname) throws Exception {
		try {
			BoardManage dto = mapper.findById(boardId);
			if(dto == null) {
				return;
			}
			
			// 게시판 정보 삭제
			mapper.deleteBoardManage(boardId);

			// 파일 테이블 삭제
			if(dto.getAttach() > 0) {
				mapper.dropTable(boardId + "_File");
			}
			
			// 답변 테이블 삭제
			if(dto.getTypeNo() == 2) {
				mapper.dropTable(boardId + "_Answer");
			}
			
			// 게시판 테이블 삭제
			mapper.dropTable(boardId);
			
			// 업로드된 파일 및 경로 삭제
			if(pathname != null) {
				fileManage.removePathname(pathname);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result = 0;
		
		try {
			result = mapper.dataCount(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return result;
	}

	@Override
	public List<BoardManage> listBoard(Map<String, Object> map) {
		List<BoardManage> list = null;
		
		try {
			list = mapper.listBoard(map);
			
			for(BoardManage dto : list) {
				dto.setBoardType(BoardType.BOARD_TYPES[dto.getTypeNo()]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public BoardManage findById(String boardId) {
		BoardManage dto = null;
		
		try {
			dto = mapper.findById(boardId);
			if(dto != null) {
				dto.setBoardType(BoardType.BOARD_TYPES[dto.getTypeNo()]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
}
