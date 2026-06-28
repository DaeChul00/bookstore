<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="container my-5">
	<div class="card shadow-sm border-0">
		<div class="card-header bg-primary text-white">
			<h3 class="mb-0">📩 문의 상세보기</h3>
		</div>

		<div class="card-body">
			<table class="table table-bordered align-middle">
				<tbody>
					<tr>
						<th class="table-light text-center" style="width: 180px;">번호</th>
						<td>${cv.id}</td>
					</tr>

					<tr>
						<th class="table-light text-center">작성자</th>
						<td><span class="badge bg-primary fs-6">${cv.userName}</span></td>
					</tr>

					<tr>
						<th class="table-light text-center">제목</th>
						<td class="fw-bold">${cv.title}</td>
					</tr>

					<tr>
						<th class="table-light text-center">카테고리</th>
						<td><span class="badge bg-secondary">${cv.category}</span></td>
					</tr>

					<tr>
						<th class="table-light text-center">상태</th>
						<td>
							<c:choose>
								<c:when test="${cv.status eq '답변대기' || cv.status eq 'WAIT'}">
									<span class="badge bg-warning text-dark">답변 대기</span>
								</c:when>
								<c:otherwise>
									<span class="badge bg-success">답변 완료</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
					    <th class="table-light text-center">내용</th>
					    <td style="height: 200px; white-space: pre-wrap;">${cv.content}</td> 
					</tr>

					<tr>
						<th class="table-light text-center">답변</th>
						<td style="height: 200px; vertical-align: top; padding: 8px;">
							<div class="text-start" style="width: 100%; text-align: left !important;">
								<c:choose>
									<c:when test="${not empty cv.answer}">
										${cv.answer}
									</c:when>
									<c:otherwise>
										<span class="text-muted">아직 답변이 등록되지 않았습니다.</span>
									</c:otherwise>
								</c:choose>
							</div>
						</td>
					</tr>

					<tr>
						<th class="table-light text-center">관리자</th>
						<td>
							<c:choose>
								<c:when test="${not empty cv.adminId}">
									${cv.adminId}
								</c:when>
								<c:otherwise>
									<span class="text-muted">-</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th class="table-light text-center">작성일</th>
						<td>${cv.createdAt}</td>
					</tr>

					<tr>
						<th class="table-light text-center">답변일</th>
						<td>
							<c:choose>
								<c:when test="${not empty cv.answeredAt}">
									${cv.answeredAt}
								</c:when>
								<c:otherwise>
									<span class="text-muted">-</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>

			<c:if test="${isAdmin}">
				<div class="container bg-light p-4 rounded border my-4">
					<h4 class="mb-3 text-primary">⚙️ 관리자 답변 작성 관제</h4>
					<form action="${pageContext.request.contextPath}/cs/answer" method="post">
						<input type="hidden" name="id" value="${param.id}">
						<div class="mb-3">
							<textarea class="form-control" name="answer" rows="4" 
									  placeholder="고객님께 남길 답변을 입력하세요. 등록 시 자동으로 '답변완료'로 전환됩니다." required>${cv.answer}</textarea>
						</div>
						<div class="text-end">
							<button type="submit" class="btn btn-success">
								${empty cv.answer ? '답변 등록하기' : '답변 수정하기'}
							</button>
						</div>
					</form>
				</div>
			</c:if>

			<div class="d-flex justify-content-center gap-2 mt-4">
				<a href="${pageContext.request.contextPath}/cs/csList" class="btn btn-secondary">목록</a>
				
				<a href="${pageContext.request.contextPath}/cs/updateform?id=${cv.id}" class="btn btn-warning">수정</a>
				<a href="${pageContext.request.contextPath}/cs/delete?id=${cv.id}" class="btn btn-danger" 
				   onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
			</div>
		</div>
	</div>
</div>