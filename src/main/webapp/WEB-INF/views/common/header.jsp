<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<style>
.search-wrapper { position: relative; width: 380px; }
.search-box { display: flex; align-items: center; width: 380px; height: 50px; border: 1px solid #ddd; border-radius: 30px; overflow: hidden; }
.search-box:focus-within { border-color: #2c3e50; box-shadow: 0 0 8px rgba(44, 62, 80, 0.2); background: #fff; }
.search-box select { border: none; background: transparent; padding: 8px; outline: none; font-size: 14px; cursor: pointer; }
.search-box input { border: none; background: transparent; padding: 10px; flex: 1; outline: none; font-size: 14px; }
.search-btn { width: 60px; height: 100%; border: none; background: #2c3e50; color: white; cursor: pointer; border-radius: 0; }
.search-icon { margin-left: 10px; color: #888; }

#searchResult { display: none; position: absolute; top: 100%; left: 0; width: 650px; background: white; border: 1px solid #ddd; z-index: 9999; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
.search-left { width: 50%; display: flex; flex-direction: column; }
.search-preview { width: 50%; padding: 20px; margin: auto; text-align: center; background: #f8f8f8; }
.search-preview img { width: 150px; height: 220px; object-fit: cover; }
.search-item { padding: 12px; border-bottom: 1px solid #eee; cursor: pointer; }
.search-item:hover { background: #f5f5f5; }
.search-item a { text-decoration: none; color: #333; }
</style>

<header style="border-bottom: 1px solid #eee; background: #fff; width: 100%;">
	<div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 15px 0;">

		<h1 style="margin: 0;">
			<a href="${pageContext.request.contextPath}/book" style="color: #2c3e50; text-decoration: none; font-weight: 800;">
				BOOKSTORE
			</a>
		</h1>

		<div class="search-wrapper">
			<form action="${pageContext.request.contextPath}/book/list" method="get" class="search-box">
				<span class="search-icon">🔍</span> 
				<select id="category" name="category">
					<option value="title">제목</option>
					<option value="author">저자</option>
					<option value="publisher">출판사</option>
				</select> 
				<input type="text" id="keyword" name="keyword" value="${param.keyword}" autocomplete="off" placeholder="책 제목, 저자 검색">
				<button type="submit" class="search-btn">검색</button>
			</form>

			<div id="searchResult">
				<div id="searchList" class="search-left"></div>
				<div class="search-preview">
					<img id="previewImage" src="" style="display: none;">
					<div class="preview-title" style="font-weight:bold; margin-top:10px;"></div>
					<div class="preview-author" style="color:#666; font-size:13px;"></div>
					<div class="preview-price" style="color:#e67e22; font-weight:bold; margin-top:5px;"></div>
				</div>
			</div>
		</div>

		<nav style="font-size: 13px; display: flex; align-items: center; gap: 10px; white-space: nowrap;">
			<a href="${pageContext.request.contextPath}/cs/csList" style="text-decoration: none; color: #333;">고객센터</a> 
			<a href="${pageContext.request.contextPath}/order/cart" style="text-decoration: none; color: #e67e22; font-weight: bold; margin-right: 5px;">🛒 장바구니</a>
			<a href="${pageContext.request.contextPath}/order/nmorderlist" style="text-decoration: none; color: #e67e22; font-weight: bold; margin-right: 5px;">📦 주문내역</a>

			<sec:authorize access="isAnonymous()">
				<a href="${pageContext.request.contextPath}/login" style="text-decoration: none; color: #333;">로그인</a>
				<a href="${pageContext.request.contextPath}/signup" style="text-decoration: none; color: #333;">회원가입</a>
			</sec:authorize>

			<sec:authorize access="isAuthenticated()">
				<sec:authorize access="not hasRole('ADMIN')">
					<a href="${pageContext.request.contextPath}/order/list" style="text-decoration: none; color: #d9534f; font-weight: bold; margin-right: 5px;">📦 주문내역</a>
				</sec:authorize>

				<c:choose>
					<c:when test="${not empty loginUser}">
						<span><strong>${loginUser.name}</strong>님</span>
					</c:when>
					<c:otherwise>
						<span><strong><sec:authentication property="principal.username" /></strong>님</span>
					</c:otherwise>
				</c:choose>
				
				<a href="${pageContext.request.contextPath}/member/update" style="color: #666; font-size: 12px; margin-right: 5px;">[정보수정]</a>

				<sec:authorize access="hasRole('ADMIN')">
					<div style="position: relative; display: inline-block;">
						<a href="#" style="text-decoration: none; color: #2980b9; font-weight: bold; padding: 10px;">
							⚙️ 관리자 메뉴 ▼
						</a>

						<div class="admin-dropdown" style="display: none; position: absolute; top: 100%; right: 0; background: white; border: 1px solid #eee; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); z-index: 1000; padding: 10px; width: 120px;">
							<a href="${pageContext.request.contextPath}/admin/book/list" style="display: block; padding: 5px 0; text-decoration: none; color: #005a32;">📚 도서관리</a> 
							<a href="${pageContext.request.contextPath}/admin/order/list" style="display: block; padding: 5px 0; text-decoration: none; color: #d35400;">📦 주문관리</a> 
							<a href="${pageContext.request.contextPath}/admin/stat/sales" style="display: block; padding: 5px 0; text-decoration: none; color: #2980b9;">📊 통계보기</a> 
							<a href="${pageContext.request.contextPath}/admin/memberList" style="display: block; padding: 5px 0; text-decoration: none; color: #2980b9;">👤 회원관리</a>
						</div>
					</div>
				</sec:authorize>

				<a href="${pageContext.request.contextPath}/logout" style="text-decoration: none; color: #333; margin-left: 10px;">로그아웃</a>
			</sec:authorize>
		</nav>
	</div>
</header>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const adminTrigger = document.querySelector('.admin-dropdown') ? document.querySelector('.admin-dropdown').previousElementSibling : null;
    const dropdown = document.querySelector('.admin-dropdown');

    if(adminTrigger && dropdown) {
        adminTrigger.parentElement.addEventListener('mouseenter', () => { dropdown.style.display = 'block'; });
        adminTrigger.parentElement.addEventListener('mouseleave', () => { dropdown.style.display = 'none'; });
    }
});
</script>