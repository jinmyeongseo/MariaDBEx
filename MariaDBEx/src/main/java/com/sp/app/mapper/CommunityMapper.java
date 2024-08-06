package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.Community;

@Mapper
public interface CommunityMapper {
	public int dataCount(Map<String, Object> map);
	public List<Community> listCommunity(Map<String, Object> map);
	public List<Community> listCommunityGroup(Map<String, Object> map);
	
	public long communityNum(String tableName);
	public void insertCommunity(Community dto) throws SQLException;
	public void updateOrderNo(Map<String, Object> map) throws SQLException;
	public void updateCommunity(Community dto) throws SQLException;
	public void deleteCommunity(Map<String, Object> map);
	public void deleteCommunityList(Map<String, Object> map);
	
	public Community findById(Map<String, Object> map);
	public void updateHitCount(Map<String, Object> map);
	public Community findByPrev(Map<String, Object> map);
	public Community findByNext(Map<String, Object> map);
	
	public Community findByQuestionAnswer(Map<String, Object> map);
	public void insertQuestionAnswer(Community dto) throws SQLException;
	public void updateQuestionAnswer(Community dto) throws SQLException;
	public void deleteQuestionAnswer(Map<String, Object> map);
	
	public void insertCommunityFile(Community dto) throws SQLException;
	public Community findByFileId(Map<String, Object> map);
	public List<Community> listCommunityFile(Map<String, Object> map);
	public void deleteCommunityFile(Map<String, Object> map) throws SQLException;
}
