<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="icon" href="data:;base64,iVBORw0KGgo=">

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/paginate.css" type="text/css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.4.2/css/all.css">
<style type="text/css">
/* body-container */
.body-container { margin: 30px auto; width: 700px; }
.body-title { width:100%; font-size: 16px; font-weight: bold; padding: 13px 0; }

.table-list thead > tr { background: #f8f9fa; }
.table-list th, .table-list td { text-align: center; }

.table-list .num { width: 60px; color: #787878; }
.table-list .subject { color: #787878; }
.table-list .name { width: 100px; color: #787878; }
.table-list .date { width: 100px; color: #787878; }
.table-list .hit { width: 70px; color: #787878; }
.table-list .answer { width: 70px; color: #787878; }
</style>

<script type="text/javascript">
function searchList() {
	const f=document.searchForm;
	f.submit();
}
</script>
</head>
<body>

<div class="body-container">
	<div class="body-title">
	    <h3>${empty boardManage.icon ? "<span>|</span>":boardManage.icon} ${boardManage.boardTitle}</h3>
	</div>

	<table class="table">
		<tr>
			<td width="50%">${dataCount}개(${page}/${total_page} 페이지)</td>
			<td align="right">&nbsp;</td>
		</tr>
	</table>
	
	<table class="table table-border table-list">
		<thead>
			<tr>
				<th class="num">번호</th>
				<th class="subject">제목</th>
				<th class="name">작성자</th>
				<th class="date">작성일</th>
				<th class="hit">조회수</th>
				<c:if test="${boardManage.typeNo==2}">
					<th class="answer">처리결과</th>
				</c:if>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach var="dto" items="${list}" varStatus="status">
				<tr>
					<td>${dataCount-(page-1)*size-status.index}</td>
					<td style="text-align: left; padding-left: 5px;">
						<c:url var="url" value="/community/${boardManage.boardId}/article/${dto.num}">
							<c:param name="page" value="${page}"/>
							<c:if test="${not empty kwd}">
								<c:param name="schType" value="${schType}"/>
								<c:param name="kwd" value="${kwd}"/>
							</c:if>
						</c:url>
						<c:forEach var="n" begin="1" end="${dto.depth}">
							&nbsp;
						</c:forEach>
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if>
						<a href="${url}">${dto.subject}</a>
					</td>
					<td>${dto.name}</td>
					<td>${dto.reg_date}</td>
					<td>${dto.hitCount}</td>
					<c:if test="${boardManage.typeNo==2}">
						<td>${dto.answerCount==0?"답변대기":"답변완료"}</td>
					</c:if>
				</tr>
			</c:forEach>
		</tbody>
		
	</table>
	
	<div class="page-navigation">
		${dataCount == 0 ? "등록된 게시글이 없습니다." : paging }
	</div>
	
	<table class="table">
		<tr>
			<td width="100">
				<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/list';">새로고침</button>
			</td>
			<td align="center">
				<form name="searchForm" action="${pageContext.request.contextPath}/community/${boardManage.boardId}/list" method="post">
					<select name="schType" class="form-select">
						<option value="all" ${schType=="all"?"selected":""}>모두</option>
						<option value="name" ${schType=="name"?"selected":""}>작성자</option>
						<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
						<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
						<option value="content" ${schType=="content"?"selected":""}>내용</option>
					</select>
					<input type="text" name="kwd" value="${kwd}" class="form-control">
					<button type="button" class="btn" onclick="searchList()">검색</button>
				</form>
			</td>
			<td align="right" width="100">
				<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/write';">글올리기</button>
			</td>
		</tr>
	</table>	
</div>

</body>
</html>