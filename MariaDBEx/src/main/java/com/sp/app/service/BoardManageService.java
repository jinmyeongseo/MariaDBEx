package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.BoardManage;

public interface BoardManageService {
	public void createBoard(BoardManage dto) throws Exception;
	public void dropBoard(String tableName, String pathname) throws Exception;
	
	public int dataCount(Map<String, Object> map);
	public List<BoardManage> listBoard(Map<String, Object> map);
	public BoardManage findById(String boardId);
}
