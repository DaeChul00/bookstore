<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .container { width: 900px; margin: 0 auto; }
    .detail-box { display: flex; margin-top: 30px; }
    .book-img { width: 250px; height: 350px; object-fit: cover; }
    .book-info { margin-left: 40px; }
    .title { font-size: 24px; font-weight: bold; }
    .meta { margin: 10px 0; color: #666; }
    .price { font-size: 20px; font-weight: bold; margin: 10px 0; }
    .content { margin-top: 20px; line-height: 1.6; }
    .btn-area { margin-top: 30px; }
    .btn { padding: 10px 15px; background-color: green; color: white; border: none; cursor: pointer; margin-right: 10px; }
</style>

<div class="container">
    <h2>도서 상세보기</h2><hr>
    <div class="detail-box">
        <img src="${bk.bookimage}" class="book-img" alt="${bk.title}">
        <div class="book-info">
            <div class="title">${bk.title}</div>
            <div class="meta">${bk.author} · ${bk.publisher} · ${bk.publictiondate}</div>
            <div class="price"><fmt:formatNumber value="${bk.price}" type="number"/>원</div>
            <div>평점 ★ ${bk.rating}</div>
            <div class="content">${bk.content}</div>
            
            <div class="btn-area">
                <c:choose>
                    <c:when test="${loginUser.role == 'ADMIN'}">
                        <button class="btn" onclick="location.href='${pageContext.request.contextPath}/book/update?id=${bk.id}'">도서 수정</button>
                        <button class="btn" style="background-color: #d9534f;" onclick="if(confirm('정말 삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/book/delete?id=${bk.id}'">도서 삭제</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn" style="background-color: #e67e22;" onclick="addCart(${bk.id})">장바구니 담기</button> 
                        <button class="btn" onclick="location.href='${pageContext.request.contextPath}/book/list'">목록으로</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
function addCart(id) {
    <c:if test="${empty loginUser}">
        alert("로그인이 필요합니다.");
        location.href = "${pageContext.request.contextPath}/login";
        return;
    </c:if>
    location.href = "${pageContext.request.contextPath}/order/addCart?bookId=" + id + "&count=1";
}
</script>