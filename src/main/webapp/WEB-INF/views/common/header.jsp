<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
.search-wrapper {
	position: relative;
	width: 380px;
}

.search-box {
	display: flex;
	align-items: center;
	width: 380px;
	height: 50px;
	border: 1px solid #ddd;
	border-radius: 30px;
	overflow: hidden;
}

.search-box:focus-within {
	border-color: #2c3e50;
	box-shadow: 0 0 8px rgba(44, 62, 80, 0.2);
	background: #fff;
}

.search-box select {
	border: none;
	background: transparent;
	padding: 8px;
	outline: none;
}

.search-box input {
	border: none;
	background: transparent;
	padding: 10px;
	flex: 1;
	outline: none;
}

.search-btn {
	width: 60px;
	height: 100%;
	border: none;
	background: #2c3e50;
	color: white;
	cursor: pointer;
	border-radius: 0;
}

.search-icon {
	margin-left: 10px;
}

/* 자동완성 결과 */
#searchResult {
	display: none;
	position: absolute;
	top: 100%;
	left: 0;
	width: 650px;
	background: white;
	border: 1px solid #ddd;
	z-index: 9999;
}

.search-left {
	width: 50%;
	display: flex;
	flex-direction: column;
}

.search-preview {
	width: 50%;
	padding: 20px;
	margin: auto;
	text-align: center;
	background: #f8f8f8;
}
.search-preview img {
	width: 150px;
	height: 220px;
	object-fit: cover;
}

.search-item {
	padding: 12px;
	border-bottom: 1px solid #eee;
	cursor: pointer;
}

.search-item:hover {
	background: #f5f5f5;
}

.search-item a {
	text-decoration: none;
	color: #333;
}
</style>
<header
	style="border-bottom: 1px solid #eee; background: #fff; width: 100%;">
	<div class="container"
		style="display: flex; justify-content: space-between; align-items: center; padding: 15px 0;">

		<!-- 로고 -->
		<h1 style="margin: 0;">
			<a href="/book"
				style="color: #2c3e50; text-decoration: none; font-weight: 800;">
				BOOKSTORE </a>
		</h1>

		<!-- 🔍 검색창 (추가된 부분 핵심) -->
		<div class="search-wrapper">

			<form action="${pageContext.request.contextPath}/book/list"
				method="get" class="search-box">

				<span class="search-icon">🔍</span> <select id="category"
					name="category">
					<option value="title">제목</option>
					<option value="author">저자</option>
					<option value="publisher">출판사</option>
				</select> <input type="text" id="keyword" name="keyword" autocomplete="off"
					placeholder="책 제목, 저자 검색">

				<button type="submit" class="search-btn">검색</button>

			</form>

			<div id="searchResult">

				<div id="searchList" class="search-left"></div>

				<div class="search-preview">
	
					<img id="previewImage" src="" style="display: none;">
				  
					<div class="preview-title"></div>

					<div class="preview-author"></div>

					<div class="preview-price"></div>

				</div>

			</div>

		</div>

		<!-- 메뉴 -->
		<nav
			style="font-size: 14px; display: flex; align-items: center; gap: 15px;">

			<a href="${pageContext.request.contextPath}/cs/csList"
				style="text-decoration: none; color: #333;">고객센터</a> <a
				href="${pageContext.request.contextPath}/order/cart"
				style="text-decoration: none; color: #e67e22; font-weight: bold;">
				🛒 장바구니 </a>

			<c:choose>
				<c:when test="${empty loginUser}">
					<a href="/login" style="text-decoration: none; color: #333;">로그인</a>
					<a href="/signup" style="text-decoration: none; color: #333;">회원가입</a>
				</c:when>

				<c:otherwise>
					<span><strong>${loginUser.name}</strong>님</span>

					<a href="${pageContext.request.contextPath}/member/update"
						style="color: #666; font-size: 12px;">[정보수정]</a>

					<a href="/order/list" style="color: #666; font-size: 12px;">[주문내역]</a>

					<c:if test="${loginUser.role == 'ADMIN'}">
						<a href="${pageContext.request.contextPath}/admin/book/list"
							style="font-weight: bold;">도서관리</a>
						<a href="${pageContext.request.contextPath}/admin/stat/sales"
							style="font-weight: bold; color: #2c3e50;">📊통계보기</a>

						<a href="${pageContext.request.contextPath}/admin/memberList"
							style="font-weight: bold;">회원관리</a>
					</c:if>

					<a href="/logout" style="color: #999;">로그아웃</a>
				</c:otherwise>
			</c:choose>
		</nav>

	</div>
</header>