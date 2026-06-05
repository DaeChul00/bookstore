<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
.cart-container { width: 1000px; margin: 50px auto; }
.cart-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
.cart-table th, .cart-table td { padding: 15px; border-bottom: 1px solid #ddd; text-align: center; }
.cart-table th { background-color: #2c3e50; color: white; }
.book-thumb { width: 60px; height: 80px; object-fit: cover; border-radius: 4px; }
.total-area { text-align: right; margin-top: 30px; font-size: 20px; font-weight: bold; }
.btn-order { background: #e67e22; color: white; padding: 12px 30px; border: none; border-radius: 6px; cursor: pointer; font-size: 16px; font-weight: bold; }
</style>

<div class="cart-container">
    <h2>🛒 마이 장바구니 목록</h2>
    
    <c:choose>
        <c:when test="${empty cartList}">
            <div style="text-align: center; padding: 50px 0; color: #777;">
                <h3>장바구니가 텅 비어 있습니다. 😊</h3>
                <a href="${pageContext.request.contextPath}/book/list" style="color: #2c3e50; font-weight: bold;">책 보러 가기 ➡️</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>이미지</th>
                        <th>도서명</th>
                        <th>판매가</th>
                        <th>수량</th>
                        <th>합계</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartList}">
                        <tr>
                            <td><img src="${item.bookimage}" class="book-thumb"></td>
                            <td style="text-align: left; font-weight: bold;">${item.title}</td>
                            <td><fmt:formatNumber value="${item.price}" type="number"/>원</td>
                            <td>
                                <input type="number" value="${item.count}" min="1" style="width: 50px; text-align: center;"
                                       onchange="handleCountChange(${item.bookId}, this.value)">
                            </td>
                            <td><fmt:formatNumber value="${item.price * item.count}" type="number"/>원</td>
                            <td>
                                <button onclick="handleDelete(${item.bookId})" style="background:#d9534f; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;">삭제</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
            <div class="total-area">
                <button class="btn-order" onclick="handleOrder()">주문하기 ➡️</button>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<form id="cartActionForm" method="post" style="display:none;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="bookId" id="actionBookId"/>
    <input type="hidden" name="count" id="actionCount"/>
</form>

<script>
// 💡 시큐리티 인증 태그를 이용해 로그인 여부를 정확히 낚아챕니다.
var isLogin = false;
<sec:authorize access="isAuthenticated()">
    isLogin = true;
</sec:authorize>

// 1. 수량 변경 통합 제어
function handleCountChange(bookId, count) {
    if (isLogin) {
        // [회원] 서버의 updateCart 컨트롤러 호출
        var form = document.getElementById("cartActionForm");
        form.action = "${pageContext.request.contextPath}/order/updateCart";
        document.getElementById("actionBookId").value = bookId;
        document.getElementById("actionCount").value = count;
        form.submit();
    } else {
        // [비회원] 브라우저 쿠키 직접 수정 후 리프레시
        updateGuestCartCookie(bookId, parseInt(count));
    }
}

// 2. 항목 삭제 통합 제어
function handleDelete(bookId) {
    if(confirm("해당 상품을 장바구니에서 삭제하시겠습니까?")) {
        if (isLogin) {
            // [회원] 서버의 deleteCart 컨트롤러 호출
            var form = document.getElementById("cartActionForm");
            form.action = "${pageContext.request.contextPath}/order/deleteCart";
            document.getElementById("actionBookId").value = bookId;
            form.submit();
        } else {
            // [비회원] 브라우저 쿠키에서 해당 품목 제거 후 리프레시
            removeGuestCartCookie(bookId);
        }
    }
}

// 3. 주문하기 누를 때 분기
function handleOrder() {
    if (!isLogin) {
        alert("로그인 후 주문이 가능합니다. 로그인 페이지로 이동합니다.");
        location.href = "${pageContext.request.contextPath}/login";
    } else {
        location.href = "${pageContext.request.contextPath}/order/buy";
    }
}

// ================= 비회원 전용 쿠키 가공 로직 유틸리티 =================

function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
    return "";
}

function updateGuestCartCookie(bookId, newCount) {
    var cartCookie = getCookie("guestCart");
    if (cartCookie) {
        var cartList = JSON.parse(decodeURIComponent(cartCookie));
        for (var i = 0; i < cartList.length; i++) {
            if (cartList[i].bookId === bookId) {
                cartList[i].count = newCount;
                break;
            }
        }
        document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
        location.reload();
    }
}

function removeGuestCartCookie(bookId) {
    var cartCookie = getCookie("guestCart");
    if (cartCookie) {
        var cartList = JSON.parse(decodeURIComponent(cartCookie));
        cartList = cartList.filter(function(item) {
            return item.bookId !== bookId;
        });
        document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
        location.reload();
    }
}
</script>