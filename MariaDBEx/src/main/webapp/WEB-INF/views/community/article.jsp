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
/* body-container */
.body-container { margin: 30px auto; width: 700px; }
.body-title { width:100%; font-size: 16px; font-weight: bold; padding: 13px 0; }

.table-article { margin-top: 20px; }
.table-article tr > td { padding-left: 5px; padding-right: 5px; }

.row-flex { display: flex; justify-content: space-between; }
.left-item {
	width:30px; margin-right: 1px;    padding:10px 10px;
    width:30px;
    text-align: center;
	font-weight: 600;
	color: #fff;
}
.right-item {
	flex-grow: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    word-spacing: nowrap;
    box-sizing: border-box;
    padding: 10px 7px;
	font-weight: 600;
	color: #fff;
}
.left-question { background: #0d6efd; }
.right-question { background: #0d6efd; }

.left-answer { background: #198754; }
.right-answer { background: #198754; }

.answer-layout { margin-top: 20px; }
.answer-form { margin-top: 5px; }
.answer-form td { padding: 7px 0; }
.answer-form tr:first-child { border-top: 1px solid #ced4da; }
.answer-form tr > td:first-child { width: 110px; text-align: center; background: #f8f9fa; }
.answer-form tr > td:nth-child(2) {	padding-left: 10px; }
.answer-form input[type=text] { width: 50%; } 
.answer-form textarea { width: 97%; height: 70px; }

.file-item { padding: 7px; margin-bottom: 3px; border: 1px solid #ced4da; color: #777777; }
</style>

<script type="text/javascript">
function deleteCommunity() {
	if(confirm("게시글을 삭제하시겠습니까 ?")) {
		let query = "num=${dto.num}&${query}";
		let url = "${pageContext.request.contextPath}/community/${boardManage.boardId}/delete?" + query;
		location.href = url;
	}
}
</script>

</head>
<body>

<div class="body-container">
	<div class="body-title">
	    <h3>${empty boardManage.icon ? "<span>|</span>":boardManage.icon} ${boardManage.boardTitle}</h3>
	</div>
	
	<table class="table table-border table-article">
		<thead>
			<c:choose>
				<c:when test="${boardManage.typeNo==2}">
					<tr style="border: none;">
						<td colspan="2" style="padding: 10px 0 0 0;">
							<div class="row-flex">
								<div class="left-item left-question">Q</div>
								<div class="right-item right-question">${dto.subject}</div>
							</div>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="2" align="center">
							<c:if test="${dto.depth!=0}">
								[Re]
							</c:if>						
							${dto.subject}
						</td>
					</tr>
				</c:otherwise>
			</c:choose>			
		</thead>
		
		<tbody>
			<tr>
				<td width="50%">
					이름 : ${dto.name}
				</td>
				<td align="right">
					${dto.reg_date } | 조회 ${dto.hitCount}
				</td>
			</tr>
			
			<tr style="${boardManage.attach > 0 ? 'border-bottom:none;':''}">
				<td colspan="2" valign="top" height="200">
					${dto.content}
				</td>
			</tr>
			
			<c:if test="${boardManage.attach > 0}">
				<tr>
					<td colspan="2">
						<c:forEach var="vo" items="${listFile}" varStatus="status">
							<p class="file-item">
								<i class="fa-regular fa-folder-open"></i>
								<a href="${pageContext.request.contextPath}/community/${boardManage.boardId}/download/${vo.fileNum}">${vo.originalFilename}</a>
								[${vo.fileSize} byte]
							</p>
						</c:forEach>
					</td>
				</tr>
			</c:if>
		<tbody>
	</table>
	
	<c:if test="${not empty answerDto}">
		<table class="table table-border table-article" style="margin-top: 0;">
			<thead>
				<tr style="border: none;">
					<td colspan="2" style="padding: 0 0 0 0;">
						<div class="row-flex">
							<div class="left-item left-answer">A</div>
							<div class="right-item right-answer">${dto.subject}</div>
						</div>
					</td>							
				</tr>
			</thead>
			<tbody>
				<tr>
					<td width="50%">
						담당자 : ${answerDto.answerName}				
					</td>
					<td align="right">
						답변일자 : ${answerDto.answer_date}
					</td>
				</tr>
				
				<tr>
					<td colspan="2" valign="top" height="150">
						${answerDto.answer}
					</td>
				</tr>
			</tbody>
		</table>
	</c:if>
		
	<table class="table table-border">
		<tr>
			<td colspan="2">
				이전글 :
				<c:if test="${not empty prevDto}">
					<a href="${pageContext.request.contextPath}/community/${boardManage.boardId}/article/${prevDto.num}?${query}">${prevDto.subject}</a>
				</c:if>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				다음글 :
				<c:if test="${not empty nextDto}">
					<a href="${pageContext.request.contextPath}/community/${boardManage.boardId}/article/${nextDto.num}?${query}">${nextDto.subject}</a>
				</c:if>
			</td>
		</tr>
	</table>
	
	<table class="table">
		<tr>
			<td width="50%">
				<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/update?num=${dto.num}&page=${page}';">수정</button>
				<c:if test="${boardManage.typeNo==1}">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/answer?num=${dto.num}&page=${page}';">답변</button>
				</c:if>
				<button type="button" class="btn" onclick="deleteCommunity();">삭제</button>
				<c:if test="${not empty answerDto}">
					<button type="button" class="btn" onclick="updateQuestionAnswer();">답변수정</button>
					<button type="button" class="btn" onclick="deleteQuestionAnswer();">답변삭제</button>
				</c:if>
			</td>
			<td align="right">
				<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/community/${boardManage.boardId}/list?${query}';">리스트</button>
			</td>
		</tr>
	</table>
	
	<c:if test="${boardManage.typeNo==2}">
		<div class="answer-layout ${not empty answerDto ? 'none':''}">
			<form name="answerForm" method="post">
				<div class='answer-header'>
					<span class="bold">답변</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
				</div>
				
				<table class="table table-border answer-form">
					<tbody>
						<tr>
							<td>작성자</td>
							<td>
								<input type="text" class='form-control' name="answerName" value="${answerDto.answerName}">
							</td>
						</tr>
						<tr>
							<td>답변</td>
							<td>
								<textarea class='form-control' name="answer">${answerDto.answer}</textarea>
							</td>
						</tr>
					</tbody>
				</table>
				<table class="table">
					<tr>
					   <td align='right'>
					   		<input type="hidden" name="num" value="${dto.num}">	
					   		<input type="hidden" name="page" value="${page}">
					   		<input type="hidden" name="mode" value="${empty answerDto ? 'answer':'update'}">				   
					        <button type="button" class="btn btnSendAnswer" onclick="sendQuestionAnswer()">${empty answerDto ? "답변 등록" : "수정 완료"}</button>
					    </td>
					 </tr>
				</table>
			</form>
		</div>
		
		<script type="text/javascript">
			function sendQuestionAnswer() {
				const f = document.answerForm;
				
			    if(! f.answerName.value.trim()) {
			        alert("이름을 입력하세요. ");
			        f.answerName.focus();
			        return;
			    }

			    if(! f.answer.value.trim()) {
			        alert("내용을 입력하세요. ");
			        f.answer.focus();
			        return;
			    }
				
				f.action = "${pageContext.request.contextPath}/community/${boardManage.boardId}/questionAnswer";
				f.submit();
			}
			
			function updateQuestionAnswer() {
				const EL = document.querySelector(".answer-layout");
				/*
				let dis = EL.style.display;
				if(dis === "block") {
					EL.style.display = "none";
				} else {
					EL.style.display = "block";
				}
				*/
				
				EL.classList.toggle("none");
			}
			
			function deleteQuestionAnswer() {
				if(confirm("답변을 삭제하시겠습니까 ?")) {
					let query = "num=${answerDto.num}&${query}";
					let url = "${pageContext.request.contextPath}/community/${boardManage.boardId}/deleteQuestionAnswer?" + query;
					location.href = url;
				}
			}
		</script>
	</c:if>
</div>

</body>
</html>