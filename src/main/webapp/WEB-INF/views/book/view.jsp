<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
.detail-container { width: 1000px; margin: 50px auto; }
.detail-card { display: flex; gap: 40px; background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.05); }
.book-img { width: 280px; height: 380px; object-fit: cover; border-radius: 10px; }
.book-info { flex: 1; }
.title { font-size: 26px; font-weight: bold; color: #2c3e50; }
.meta { margin: 10px 0; color: #777; font-size: 14px; }
.price { font-size: 22px; font-weight: bold; color: #e67e22; margin: 15px 0; }
.rating { color: #f39c12; font-weight: bold; margin-bottom: 15px; }
.content { margin-top: 20px; line-height: 1.6; color: #555; }
.btn-area { margin-top: 30px; }
.btn { padding: 10px 18px; border-radius: 6px; border: none; font-weight: bold; cursor: pointer; text-decoration: none; font-size: 14px; margin-right: 10px; }
.btn-primary { background: #2c3e50; color: white; }
.btn-primary:hover { background: #1a252f; }
.btn-danger { background: #d9534f; color: white; }
.btn-warning { background: #e67e22; color: white; }
.btn-secondary { background: #aaa; color: white; }
</style>

<div class="detail-container">
    <div class="detail-card">
        <img src="${bk.bookimage}" class="book-img" alt="${bk.title}">
        <div class="book-info">
            <div class="title">${bk.title}</div>
            <div class="meta">${bk.author} · ${bk.publisher} · ${bk.publictiondate}</div>
            <div class="price"><fmt:formatNumber value="${bk.price}" type="number"/>원</div>
            <div class="rating">★ ${bk.rating}</div>
            <div class="content">${bk.content}</div>

            <div class="btn-area">
                <sec:authorize access="hasRole('ADMIN')">
                    <button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/book/update?id=${bk.id}'">수정</button>
                    <button class="btn btn-danger" onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/book/delete?id=${bk.id}'">삭제</button>
                </sec:authorize>

                <sec:authorize access="isAnonymous() or hasRole('USER')">
                    <button class="btn btn-warning" onclick="submitCartForm()">🛒 장바구니</button>
                    <button class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/book/list'">목록</button>
                </sec:authorize>
            </div>
        </div>
    </div>
</div>

<form id="hiddenCartForm" action="${pageContext.request.contextPath}/order/addCart" method="post" style="display:none;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="bookId" value="${bk.id}" />
    <input type="hidden" name="count" value="1" />
</form>

<script>
function submitCartForm() {
    var isAnonymous = true;
    <sec:authorize access="isAuthenticated()">
        isAnonymous = false;
    </sec:authorize>

    if (isAnonymous) {
        alert("로그인이 필요합니다.");
        location.href = "${pageContext.request.contextPath}/login";
        return;
    }
    // 히든 POST 폼 구동 제출!
    document.getElementById("hiddenCartForm").submit();
}
</script>