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
                    <button class="btn btn-warning" onclick="handleCart(${bk.id})">🛒 장바구니</button>
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
// 💡 실시간 로그인 판별 플래그 배치
var isLogin = false;
<sec:authorize access="isAuthenticated()">
    isLogin = true;
</sec:authorize>

function handleCart(bookId) {
    if (isLogin) {
        // 1. [회원 파트] 시큐리티 인증 토큰이 실린 히든 POST 폼 서브밋 실행
        document.getElementById("hiddenCartForm").submit();
    } else {
        // 2. [비회원 파트] 대철이가 설계한 순수 브라우저 쿠키 적재 모듈 실행
        addGuestCartToCookie(bookId, 1);
    }
}

// 쿠키 핸들러 함수 유틸리티
function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
    return "";
}

function addGuestCartToCookie(bookId, count) {
    var cartCookie = getCookie("guestCart");
    var cartList = [];
    
    if (cartCookie) {
        // 기존 쿠키가 있다면 JSON 파싱
        cartList = JSON.parse(decodeURIComponent(cartCookie));
    }
    
    // 이미 바구니에 같은 책이 있는지 검사
    var isExist = false;
    for (var i = 0; i < cartList.length; i++) {
        if (cartList[i].bookId === bookId) {
            cartList[i].count += count;
            isExist = true;
            break;
        }
    }
    
    // 새 상품이면 푸시
    if (!isExist) {
        cartList.push({ "bookId": bookId, "count": count });
    }
    
    // 💡 쿠키 유효기간 30일 설정 및 인코딩하여 저장
    document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
    
    if (confirm("장바구니에 상품이 담겼습니다.\n장바구니 페이지로 이동하시겠습니까?")) {
        location.href = "${pageContext.request.contextPath}/order/cart";
    }
}
</script>