<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
/* 기존 스타일은 그대로 유지합니다 */
.search-box { display: flex; align-items: center; background: #f8f9fa; border-radius: 50px; padding: 5px; border: 1px solid #ddd; transition: all 0.3s ease; }
.search-box:focus-within { border-color: #2c3e50; box-shadow: 0 0 8px rgba(44, 62, 80, 0.2); background: #fff; }
.search-box select { border: none; background: transparent; padding: 8px; font-size: 14px; outline: none; cursor: pointer; }
.search-box input { border: none; background: transparent; padding: 10px; width: 220px; outline: none; font-size: 14px; }
.search-btn { background: #2c3e50; border: none; color: white; padding: 8px 15px; border-radius: 50px; cursor: pointer; transition: 0.2s; }
.search-btn:hover { background: #1a252f; }
.search-icon { margin-left: 10px; color: #888; }
</style>

<%-- 
==========================================================================
[원본 코드] JSTL을 사용했던 기존 헤더 (스프링 시큐리티 적용 전)
==========================================================================
<header style="border-bottom: 1px solid #eee; background: #fff; width: 100%;">
	<div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 15px 0;">
		<h1 style="margin: 0;">
			<a href="/book" style="color: #2c3e50; text-decoration: none; font-weight: 800;">BOOKSTORE</a>
		</h1>

		<form action="${pageContext.request.contextPath}/book/list" method="get" class="search-box">
			<span class="search-icon">🔍</span> 
            <select name="category">
				<option value="title">제목</option>
				<option value="author">저자</option>
				<option value="publisher">출판사</option>
			</select> 
            <input type="text" name="keyword" value="${param.keyword}" placeholder="책 제목, 저자 검색">
			<button type="submit" class="search-btn">검색</button>
		</form>

		<nav style="font-size: 14px; display: flex; align-items: center; gap: 15px;">
			<a href="${pageContext.request.contextPath}/cs/csList" style="text-decoration: none; color: #333;">고객센터</a>
			<a href="${pageContext.request.contextPath}/order/cart" style="text-decoration: none; color: #e67e22; font-weight: bold;">🛒 장바구니</a>

			<c:choose>
				<c:when test="${empty loginUser}">
					<a href="/login" style="text-decoration: none; color: #333;">로그인</a>
					<a href="/signup" style="text-decoration: none; color: #333;">회원가입</a>
				</c:when>
				<c:otherwise>
					<span><strong>${loginUser.name}</strong>님</span>
					<a href="${pageContext.request.contextPath}/member/update" style="color: #666; font-size: 12px;">[정보수정]</a>

					<c:if test="${loginUser.role == 'ADMIN'}">
						<a href="${pageContext.request.contextPath}/admin/book/list" style="font-weight: bold;">도서관리</a>
						<a href="${pageContext.request.contextPath}/admin/stat/sales" style="font-weight: bold; color: #2c3e50;">📊통계보기</a>
						<a href="${pageContext.request.contextPath}/admin/memberList" style="font-weight: bold;">회원관리</a>
					</c:if>
					<a href="/logout" style="color: #999;">로그아웃</a>
				</c:otherwise>
			</c:choose>
		</nav>
	</div>
</header>
--%>


<header style="border-bottom: 1px solid #eee; background: #fff; width: 100%;">
    <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 15px 0;">

        <h1 style="margin: 0;">
            <a href="/book" style="color: #2c3e50; text-decoration: none; font-weight: 800;">
                BOOKSTORE 
            </a>
        </h1>

        <form action="${pageContext.request.contextPath}/book/list" method="get" class="search-box">
            <span class="search-icon">🔍</span> 
            <select name="category">
                <option value="title">제목</option>
                <option value="author">저자</option>
                <option value="publisher">출판사</option>
            </select> 
            <input type="text" name="keyword" value="${param.keyword}" placeholder="책 제목, 저자 검색">
            <button type="submit" class="search-btn">검색</button>
        </form>

        <nav style="font-size: 14px; display: flex; align-items: center; gap: 15px;">
    <a href="${pageContext.request.contextPath}/cs/csList" style="text-decoration: none; color: #333;">고객센터</a>
    <a href="${pageContext.request.contextPath}/order/cart" style="text-decoration: none; color: #e67e22; font-weight: bold;">🛒 장바구니</a>

    <c:choose>
        <%-- 1. 로그인 상태인 경우 (카카오 세션 또는 일반 로그인) --%>
        <c:when test="${not empty sessionScope.loginUser || pageContext.request.userPrincipal != null}">
            <span>
                <strong>
                    <c:choose>
                        <c:when test="${not empty sessionScope.loginUser}">
                            ${sessionScope.loginUser.name}
                        </c:when>
                        <c:otherwise>
                            <sec:authentication property="principal.username" />
                        </c:otherwise>
                    </c:choose>
                </strong>님
            </span>

            <a href="${pageContext.request.contextPath}/member/update" style="color: #666; font-size: 12px;">[정보수정]</a>

            <%-- 관리자 권한 확인 --%>
            <sec:authorize access="hasRole('ADMIN')">
                <a href="${pageContext.request.contextPath}/admin/book/list" style="font-weight: bold;">도서관리</a>
                <a href="${pageContext.request.contextPath}/admin/stat/sales" style="font-weight: bold; color: #2c3e50;">📊통계보기</a>
                <a href="${pageContext.request.contextPath}/admin/memberList" style="font-weight: bold;">회원관리</a>
            </sec:authorize>

            <a href="/logout" style="color: #999;">로그아웃</a>
        </c:when>

        <%-- 2. 비로그인 상태 --%>
        <c:otherwise>
            <a href="/login" style="text-decoration: none; color: #333;">로그인</a>
            <a href="/signup" style="text-decoration: none; color: #333;">회원가입</a>
        </c:otherwise>
    </c:choose>
</nav>
    </div>
</header>