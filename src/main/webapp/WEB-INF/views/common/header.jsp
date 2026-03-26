<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header style="border-bottom: 1px solid var(--bs-border); background: #fff; width: 100%;">
    <div class="container" style="display:flex; justify-content:space-between; align-items:center; padding: 15px 0;">
        <h1 style="margin:0;"><a href="/book/list" style="color:var(--bs-main); text-decoration:none; font-weight:800; letter-spacing:-1px;">BOOKSTORE</a></h1>
        <nav style="font-size:14px; display: flex; align-items: center; gap: 20px;">
            <c:choose>
                <c:when test="${empty loginUser}">
                    <a href="/login" style="text-decoration:none; color:#333;">로그인</a>
                    <a href="/signup" style="text-decoration:none; color:#333;">회원가입</a>
                </c:when>
                <c:otherwise>
                    <span><strong>${loginUser.name}</strong>님</span>
					<a href="${pageContext.request.contextPath}/member/update" style="text-decoration:none; color:#666; font-size:12px; margin-left:10px;">[정보수정]</a>
                    <c:if test="${loginUser.role == 'ADMIN'}">
					    <a href="${pageContext.request.contextPath}/admin/book/list" 
					       style="color:var(--kb-green); font-weight:bold; text-decoration:none;">도서관리</a>
					       
					    <a href="${pageContext.request.contextPath}/admin/memberList" 
					       style="color:#333; font-weight:bold; text-decoration:none;">회원관리</a>
					       
						<a href="${pageContext.request.contextPath}/admin/stat/sales" 
					       style="color:#333; font-weight:bold; text-decoration:none;">통계보기</a>
					</c:if>
                    <a href="/logout" style="color:#999; text-decoration:none;">로그아웃</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>