<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="container bg-white p-5 rounded shadow-sm">
	<h2 class="text-center mb-4">문의사항 작성</h2>

	<div class="d-flex justify-content-between mb-3">
		<div></div>
		<button class="btn btn-secondary" onclick="location.href='list'">목록</button>
	</div>

	<form action="${pageContext.request.contextPath}/cs/insert"
		method="post">

		<table class="table border-top">
			<tbody>

				<!-- 카테고리 -->
				<tr>
					<th style="width: 120px;" class="table-light text-center">카테고리</th>
					<td><select name="category" class="form-select">
							<option value="일반">일반</option>
							<option value="결제">결제</option>
							<option value="기술">기술</option>
							<option value="기타">기타</option>
					</select></td>
				</tr>

				<!-- 제목 -->
				<tr>
					<th class="table-light text-center">제목</th>
					<td><input type="text" name="title" class="form-control"
						placeholder="제목을 입력하세요" required></td>
				</tr>

				<!-- 작성자 -->
				<tr>
					<th class="table-light text-center">작성자</th>
					<td><input type="text" name="userName" class="form-control"
						placeholder="작성자 이름" required></td>
				</tr>

				<!-- 내용 -->
				<tr>
					<th class="table-light text-center">내용</th>
					<td><textarea name="content" class="form-control" rows="6"
							placeholder="문의 내용을 입력하세요" required></textarea></td>
				</tr>

			</tbody>
		</table>

		<!-- 하단 버튼 -->
		<div class="d-flex justify-content-end mt-3">
			<button type="reset" class="btn btn-outline-danger me-2">초기화</button>
			<button type="submit" class="btn btn-primary">등록</button>
		</div>

	</form>
</div>