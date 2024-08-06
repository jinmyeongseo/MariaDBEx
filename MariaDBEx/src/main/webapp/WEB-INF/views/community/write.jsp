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
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.4.2/css/all.css">
<style type="text/css">
/* container */
.body-container { margin: 30px auto; width: 700px; }
.body-title { width:100%; font-size: 16px; font-weight: bold; padding: 13px 0; }

.table-form { margin-top: 20px; }
.table-form td { padding: 7px 0; }
.table-form tr:first-child {  border-top: 2px solid #212529; }
.table-form tr > td:first-child { width: 110px; text-align: center; background: #f8f9fa; }
.table-form tr > td:nth-child(2) { 	padding-left: 10px; }
.table-form input[type=text], .table-form input[type=file], .table-form textarea { width: 97%; }
.table-form input[type=password] { width: 50%; }

.delete-file { cursor: pointer; }
.delete-file:hover { color: #0d58ba; }
</style>

<script type="text/javascript" src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
function sendOk() {
    const f = document.boardForm;

    let str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.name.value.trim();
    if(!str) {
        alert("이름을 입력하세요. ");
        f.name.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }
	
    f.action = "${pageContext.request.contextPath}/community/${boardManage.boardId}/${mode}";
    f.submit();
}
</script>
</head>
<body>

<div class="body-container">
	<div class="body-title">
	    <h3>${empty boardManage.icon ? "<span>|</span>":boardManage.icon} ${boardManage.boardTitle}</h3>
	</div>

	<form name="boardForm" method="post" enctype="multipart/form-data">
		<table class="table table-border table-form">
			<tr> 
				<td>제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
				<td> 
					<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
				</td>
			</tr>
			
			<tr> 
				<td>작성자</td>
				<td> 
					<input type="text" name="name" maxlength="10" class="form-control" value="${dto.name}">
				</td>
			</tr>
			
			<tr> 
				<td>내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
				<td valign="top"> 
					<textarea name="content" class="form-control">${dto.content}</textarea>
				</td>
			</tr>
			
			<c:if test="${boardManage.attach > 0}">
				<tr>
					<td>첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
					<td> 
						<input type="file" name="selectFile" class="form-control" ${boardManage.attach > 1 ? "multiple":""} >
					</td>
				</tr>
			</c:if>
			
			<c:if test="${mode=='update'}">
				<c:forEach var="vo" items="${listFile}">
					<tr> 
						<td>첨부된파일</td>
						<td> 
							<span class="delete-file" data-fileNum="${vo.fileNum}"><i class="fa-solid fa-trash-can"></i></span> 
							${vo.originalFilename}
						</td>
					  </tr>
				</c:forEach>
			</c:if>				
		</table>
			
		<table class="table">
			<tr> 
				<td align="center">
					<button type="button" class="btn" onclick="sendOk();">${mode=="update"?"수정완료":"등록완료"}</button>
					<button type="reset" class="btn">다시입력</button>
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/list';">${mode=="update"?"수정취소":"등록취소"}</button>
					<c:if test="${mode=='update'}">
						<input type="hidden" name="num" value="${dto.num}">
						<input type="hidden" name="page" value="${page}">
					</c:if>
					<c:if test="${mode=='answer'}">
						<input type="hidden" name="groupNum" value="${dto.groupNum}">
						<input type="hidden" name="orderNo" value="${dto.orderNo}">
						<input type="hidden" name="depth" value="${dto.depth}">
						<input type="hidden" name="page" value="${page}">
					</c:if>
				</td>
			</tr>
		</table>

	</form>
	
</div>

<c:if test="${mode=='update'}">
	<script type="text/javascript">
		$(".delete-file").click(function(){
			if(! confirm('선택한 파일을 삭제 하시겠습니까 ? ')) {
				return false;
			}
			
			let $tr = $(this).closest("tr");
			let fileNum = $(this).attr("data-fileNum");
			let url = "${pageContext.request.contextPath}/community/${boardManage.boardId}/deleteFile";
			$.post(url, {fileNum:fileNum}, function(data){
				$($tr).remove();
			}, "json").fail(function(){
				alert("error...");
			});
		});
	</script>
</c:if>

</body>
</html>