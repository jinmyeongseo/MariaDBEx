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
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/popup.css" type="text/css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.4.2/css/all.css">
<style type="text/css">
/* body-container */
.body-container { margin: 30px auto; width: 800px; }
.body-title { width:100%; font-size: 16px; font-weight: bold; padding: 13px 0; }

.table-list thead > tr { background: #f8f9fa; }
.table-list th, .table-list td { text-align: center; }
/* .table-list td:nth-child(6n+3) { text-align: left; padding-left: 5px; } */

.table-list .th-num { width: 60px; color: #787878; }
.table-list .th-id { width: 120px; color: #787878; }
.table-list .th-title { color: #787878; }
.table-list .th-type { width: 100px; color: #787878; }
.table-list .th-file { width: 80px; color: #787878; }
.table-list .th-list { width: 110px; color: #787878; }
.table-list .th-manage { width: 135px; color: #787878; }

.table-form { margin-top: 20px; }
.table-form td { padding: 7px 0; }
.table-form tr:first-child {  border-top: 2px solid #212529; }
.table-form tr > td:first-child { width: 110px; text-align: center; background: #f8f9fa; }
.table-form tr > td:nth-child(2) { 	padding-left: 10px; }
.table-form input[type=text], .table-form input[type=file], .table-form textarea { width: 97%; }
.table-form input[type=password] { width: 50%; }
</style>

<script type="text/javascript">
function deleteBoard(boardId) {
	if(confirm('게시판 및 게시판의 게시글을 삭제하시겠습니까 ? ')) {
		let q = "boardId=" + boardId + "&page=${page}";
		location.href = "${pageContext.request.contextPath}/boardManage/drop?" + q;
	}
}
</script>

</head>
<body>

<div class="body-container">
	<div class="body-title">
	    <h3><i class="fa-regular fa-rectangle-list"></i> 게시판 관리</h3>
	</div>

	<table class="table">
		<tr>
			<td width="50%">${dataCount}개(${page}/${total_page} 페이지)</td>
			<td align="right">
				<button type="button" class="btn" id="modal-open">게시판 생성</button>
			</td>
		</tr>
	</table>
	
	<table class="table table-border table-list">
		<thead>
			<tr>
				<th class="th-num">번호</th>
				<th class="th-id">게시판 아이디</th>
				<th class="th-title">게시판 이름</th>
				<th class="th-type">게시판 타입</th>
				<th class="th-file">첨부</th>
				<th class="th-list">게시글 리스트</th>
				<th class="th-manage">설정</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach var="dto" items="${list}" varStatus="status">
				<tr>
					<td>${dataCount-(page-1)*size-status.index}</td>
					<td>${dto.boardId}</td>
					<td>${dto.boardTitle}</td>
					<td>${dto.boardType}</td>
					<td>
						${dto.attach==0?"불가":(dto.attach==1?"단일파일":"다중파일")}
					</td>
					<td>
						<c:url var="url" value="/community/${dto.boardId}/list"/>
						<button type="button" class="btn" onclick="location.href='${url}';">바로가기</button>
					</td>
					<td>
						<button type="button" class="btn">수정</button>
						<button type="button" class="btn" onclick="deleteBoard('${dto.boardId}');">삭제</button>
					</td>
				</tr>
			</c:forEach>
		<tbody>
		
	</table>
	
	<div class="page-navigation">
		${dataCount==0 ? "등록된 자료가 없습니다." : paging }
	</div>
</div>

<div class="popup-wrap" id="modal-container">
	<div class="popup-content">
		<div class="popup-header">
			<h3 class="popup-title">게시판 생성</h3>
			<button type="button" class="btn-popup btn-icon popup-close" > <i class="fa-solid fa-xmark"></i> </button>
   		</div>
		<div class="popup-body">
			<form name="boardManageForm" method="post">
			
				<table class="table table-border table-form">
					<tr> 
						<td>게시판 아이디</td>
						<td>
							<p>
								<input type="text" name="boardId" maxlength="50" class="form-control">
							</p>
							<p class="help-block">게시판 아이디는 3~15자 이내의 소문자만 가능합니다.</p>
						</td>
					</tr>

					<tr>
						<td>게시판 제목</td>
						<td>
							<input type="text" name="boardTitle" maxlength="50" class="form-control">
						</td>
					</tr>

					<tr>
						<td>게시판 아이콘</td>
						<td>
							<p>
								<input type="text" name="icon" class="form-control">
							</p>
							<p class="help-block">
								<c:out value="아이콘 예 : <i class='fa-regular fa-square'></i>"></c:out>
							</p>
						</td>
					</tr>

					<tr> 
						<td>게시판 타입</td>
						<td> 
							<select name="typeNo" class="form-select">
								<c:forEach var="type" items="${BOARD_TYPES}" varStatus="status">
									<option value="${status.index}">${type}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td>파일 첨부</td>
						<td> 
							<select name="attach" class="form-select">
								<option value="0">지원안함</option>
								<option value="1">단일파일</option>
								<option value="2">다중파일</option>
							</select>
						</td>
					</tr>

					<tr>
						<td>사용 여부</td>
						<td> 
							<input type="radio" name="enabled" id="enabled1" value="1" checked> <label for="enabled1">사용 가능</label>
							<input type="radio" name="enabled" id="enabled2" value="0"> <label for="enabled2">사용 불가</label>
						</td>
					</tr>

				</table>
			</form>
		</div>
		<div class="popup-footer">
			<button class="btn-popup popup-close" id="modal-close">닫기</button>
			<button class="btn-popup confirm" id="modal-confirm">생성 하기</button>
		</div>
	</div>
</div>
	
<script src="${pageContext.request.contextPath}/resources/js/popup.js"></script>
<script type="text/javascript">
window.addEventListener('load', (e) => {
	const btnOpen = document.querySelector('#modal-open');
	const btnConfirm = document.querySelector('#modal-confirm');
	
	btnOpen.addEventListener('click', () => {
		modalOpen('#modal-container');
	});

	btnConfirm.addEventListener('click', () => {
		// modalClose('#modal-container');
		
		const f = document.boardManageForm;
		
		if(! f.boardTitle.value.trim()) {
			alert("게시판 제목을 입력 하세요");
			f.boardTitle.focus();
			return;
		}
		
		if(! /^[a-z]{3,15}$/.test(f.boardId.value)) {
			alert("게시판 아이디는 3~15자 이내의 소문자만 가능합니다.");
			f.boardId.focus();
			return;
		}
		
		f.action = "${pageContext.request.contextPath}/boardManage/create";
		f.submit();
	});
	
});
</script>

</body>
</html>