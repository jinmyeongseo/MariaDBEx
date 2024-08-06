package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.BoardManage;

@Mapper
public interface BoardManageMapper {
	public int dataCount(Map<String, Object> map);
	public List<BoardManage> listBoard(Map<String, Object> map);
	public BoardManage findById(String boardId);
	
	public void insertBoardManage(BoardManage dto) throws SQLException;
	public void updateBoardManage(BoardManage dto) throws SQLException;
	public void deleteBoardManage(String boardId) throws SQLException;
	
	public void createBoardTable(Map<String, Object> map) throws SQLException;
	public void createBoardFileTable(Map<String, Object> map) throws SQLException;
	public void createAnswerTable(Map<String, Object> map) throws SQLException;

	public void dropTable(String tableName) throws SQLException;
}
