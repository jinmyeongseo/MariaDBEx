package com.sp.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sp.app.common.FileManager;
import com.sp.app.domain.Community;
import com.sp.app.mapper.CommunityMapper;

@Service
public class CommunityServiceImpl implements CommunityService {
	@Autowired
	private CommunityMapper mapper;
	
	@Autowired
	private FileManager fileManager;	
	
	@Override
	public void insertCommunity(Community dto, String mode, String pathname) throws Exception {
		try {
			long maxNum = mapper.communityNum(dto.getTableName());
			
			if (mode.equals("write")) { // 새글등록시
				dto.setNum(maxNum + 1);
				dto.setGroupNum(maxNum + 1);
				dto.setDepth(0);
				dto.setOrderNo(0);
			} else { // 답글 등록시
				// orderNo 변경
				Map<String, Object> map = new HashMap<>();
				map.put("tableName", dto.getTableName());
				map.put("groupNum", dto.getGroupNum());
				map.put("orderNo", dto.getOrderNo());
				mapper.updateOrderNo(map);

				dto.setNum(maxNum + 1);
				dto.setDepth(dto.getDepth() + 1);
				dto.setOrderNo(dto.getOrderNo() + 1);
			}
			
			mapper.insertCommunity(dto);
			
			if(dto.getAttach() == 0) {
				return;
			}
			
			// 파일 업로드
			if (! dto.getSelectFile().isEmpty()) {
				for (MultipartFile mf : dto.getSelectFile()) {
					String saveFilename = fileManager.doFileUpload(mf, pathname);
					if (saveFilename == null) {
						continue;
					}

					String originalFilename = mf.getOriginalFilename();
					long fileSize = mf.getSize();

					dto.setOriginalFilename(originalFilename);
					dto.setSaveFilename(saveFilename);
					dto.setFileSize(fileSize);
					
					mapper.insertCommunityFile(dto);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateCommunity(Community dto, String pathname) throws Exception {
		try {
			mapper.updateCommunity(dto);
			
			if(dto.getAttach() == 0) {
				return;
			}
			
			if (! dto.getSelectFile().isEmpty()) {
				// 하나 파일 업로드만 가능한 경우 기존 파일 지우기
				if(dto.getAttach() == 1) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("tableName", dto.getTableName());
					map.put("num", dto.getNum());
					List<Community> listFile = mapper.listCommunityFile(map);
					
					for(Community vo : listFile) {
						fileManager.doFileDelete(vo.getSaveFilename(), pathname);
					}
					
					map.put("field", "num");
					mapper.deleteCommunityFile(map);
				}
				
				for (MultipartFile mf : dto.getSelectFile()) {
					String saveFilename = fileManager.doFileUpload(mf, pathname);
					if (saveFilename == null) {
						continue;
					}

					String originalFilename = mf.getOriginalFilename();
					long fileSize = mf.getSize();

					dto.setOriginalFilename(originalFilename);
					dto.setSaveFilename(saveFilename);
					dto.setFileSize(fileSize);

					mapper.insertCommunityFile(dto);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void deleteCommunity(Map<String, Object> map, String pathname) throws Exception {
		try {
			String tableName = (String)map.get("tableName");
			int typeNo = (Integer)map.get("typeNo");
			int attach = (Integer)map.get("attach");
			
			// 답변형 게시판에서 게시글을 삭제할 경우 자신과 하위 게시글 번호
			List<Long> listNums = new ArrayList<Long>();
			boolean isFirst = true;
			int depth = 0;
			List<Community> list = mapper.listCommunityGroup(map);
			for(Community dto : list) {
				if(isFirst) {
					isFirst = false;
					depth = dto.getDepth();
					listNums.add(dto.getNum()); // 삭제할 게시글
					continue;
				}
				
				if(depth < dto.getDepth()) {
					listNums.add(dto.getNum()); // 삭제할 게시글
				} else {
					break;
				}
			}
			
			// 문의 사항 게시글인 경우
			if(typeNo == 2) {
				mapper.deleteQuestionAnswer(map);
			}

			// 파일 첨부인 경우
			if(attach > 0) {
				Map<String, Object> fmap = new HashMap<String, Object>();
				fmap.put("tableName", tableName);
				map.put("field", "num");
				
				for(long num : listNums) {
					map.put("num", num);
					List<Community> listFile = mapper.listCommunityFile(map);
					
					for(Community vo : listFile) {
						fileManager.doFileDelete(vo.getSaveFilename(), pathname);
					}
					
					mapper.deleteCommunityFile(map);
				}
			}
			
			map.put("list", listNums);
			mapper.deleteCommunityList(map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
	public List<Community> listCommunity(Map<String, Object> map) {
		List<Community> list = null;
		
		try {
			list = mapper.listCommunity(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public Community findById(Map<String, Object> map) {
		Community dto = null;
		
		try {
			dto = mapper.findById(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}

	@Override
	public void updateHitCount(Map<String, Object> map) throws Exception {
		try {
			mapper.updateHitCount(map);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public Community findByPrev(Map<String, Object> map) {
		Community dto = null;
		
		try {
			dto = mapper.findByPrev(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}

	@Override
	public Community findByNext(Map<String, Object> map) {
		Community dto = null;
		
		try {
			dto = mapper.findByNext(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public void insertQuestionAnswer(Community dto) throws Exception {
		try {
			mapper.insertQuestionAnswer(dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateQuestionAnswer(Community dto) throws Exception {
		try {
			mapper.updateQuestionAnswer(dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void deleteQuestionAnswer(Map<String, Object> map) throws Exception {
		try {
			mapper.deleteQuestionAnswer(map);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public Community findByQuestionAnswer(Map<String, Object> map) {
		Community dto = null;
		
		try {
			dto = mapper.findByQuestionAnswer(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public List<Community> listCommunityFile(Map<String, Object> map) {
		List<Community> list = null;
		
		try {
			list = mapper.listCommunityFile(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public Community findByFileId(Map<String, Object> map) {
		Community dto = null;
		
		try {
			dto = mapper.findByFileId(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public void deleteCommunityFile(Map<String, Object> map, String pathname) throws Exception {
		try {
			Community dto = findByFileId(map);
			
			if (dto != null) {
				fileManager.doFileDelete(dto.getSaveFilename(), pathname);
			}
			
			mapper.deleteCommunityFile(map);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
