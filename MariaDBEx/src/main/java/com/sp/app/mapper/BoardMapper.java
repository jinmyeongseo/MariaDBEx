package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.Board;

/*
* @Mapper
  : interface를 매퍼로 등록하기 위해 사용
  : MyBatis 3.4.0 에서 추가
  : @Mapper 어노테이션을 선언한 interface를 생성하면 따로 implements(구현) 생략
  : @Mapper 어노테이션을 선언한 인터페이스 메소드 이름과 MyBatis Mapper에 작성한 Sql ID는 반드시 일치해야 함
  : MyBatis Mapper의 namespace는 인터페이스의 풀 패키지 경로를 입력
  : <mybatis:scan/>, @MapperScan 에서  base-package / basePackages 프로퍼티만 사용할 때는
     @Mapper 어노테이션을 사용하지 않아도 자동으로 패키지 하위의 인터페이스는 매퍼로 등록
  : @Repository 를 사용해도 가능
    -해당 객체는 자동으로 빈 등록
    - DAO 클래스에서 사용
    - DB Exception을 DataAccessException으로 변환   
    - SqlSession을 이용하여 DB작업할 때 사용
* 흐름
  Controller  -> Service -> Repository
  Controller  -> Service -> Repository -> Mapper
  Controller  -> Service -> Mapper
*/

//@Repository // 가능
@Mapper // 생략 가능
public interface BoardMapper {
	public void insertBoard(Board dto) throws SQLException;
	public void updateBoard(Board dto) throws SQLException;
	public void deleteBoard(long num) throws SQLException;
	public void updateHitCount(long num) throws SQLException;
	
	public int dataCount(Map<String, Object> map);
	public List<Board> listBoard(Map<String, Object> map);
	public Board findById(long num);
	public Board findByPrev(Map<String, Object> map);
	public Board findByNext(Map<String, Object> map);
}
