package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.Community;

public interface CommunityService {
	public void insertCommunity(Community dto, String mode, String pathname) throws Exception;
	public void updateCommunity(Community dto, String pathname) throws Exception;
	public void deleteCommunity(Map<String, Object> map, String pathname) throws Exception;

	public int dataCount(Map<String, Object> map);
	public List<Community> listCommunity(Map<String, Object> map);

	public void updateHitCount(Map<String, Object> map) throws Exception;
	public Community findById(Map<String, Object> map);
	public Community findByPrev(Map<String, Object> map);
	public Community findByNext(Map<String, Object> map);
	
	public void insertQuestionAnswer(Community dto) throws Exception;
	public void updateQuestionAnswer(Community dto) throws Exception;
	public void deleteQuestionAnswer(Map<String, Object> map) throws Exception;
	public Community findByQuestionAnswer(Map<String, Object> map);
	
	public List<Community> listCommunityFile(Map<String, Object> map);
	public Community findByFileId(Map<String, Object> map);
	public void deleteCommunityFile(Map<String, Object> map, String pathname) throws Exception;
}
