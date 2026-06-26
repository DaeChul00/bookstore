<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script src="https://js.tosspayments.com/v1/payment"></script>

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
    
    <input type="hidden" name="cartId" id="actionBookId"/>
    <input type="hidden" name="count" id="actionCount"/>
</form>

<script>
var isLogin = false;
<sec:authorize access="isAuthenticated()">
    isLogin = true;
</sec:authorize>

function handleCountChange(bookId, count) {
    if (count < 1) {
        alert("최소 수량은 1개입니다.");
        return;
    }

    if (isLogin) {
        var url = "${pageContext.request.contextPath}/order/updateCartAsync";
        var csrfParameterName = "${_csrf.parameterName}";
        var csrfToken = "${_csrf.token}";

        var formData = new URLSearchParams();
        formData.append("bookId", bookId);
        formData.append("count", count);    
        formData.append(csrfParameterName, csrfToken);

        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData.toString()
        })
        .then(function(response) { 
            return response.text(); // 🛠️ 순수 텍스트 응답 처리
        })
        .then(function(data) {
            if (data === "success") {
                location.reload();
            } else {
                alert("수량 변경 반영에 실패했습니다.");
            }
        })
        .catch(function(error) {
            console.error("Error:", error);
            alert("서버 통신 중 오류가 발생했습니다.");
        });

    } else {
        updateGuestCartCookie(bookId, parseInt(count));
    }
}

function handleDelete(bookId) {
    if(confirm("해당 상품을 장바구니에서 삭제하시겠습니까?")) {
        if (isLogin) {
            var form = document.getElementById("cartActionForm");
            form.action = "${pageContext.request.contextPath}/order/deleteCart";
            document.getElementById("actionBookId").value = bookId;
            form.submit();
        } else {
            removeGuestCartCookie(bookId);
        }
    }
}

function handleOrder() {
    if (!isLogin) {
        alert("로그인 후 주문이 가능합니다.");
        location.href = "${pageContext.request.contextPath}/login";
        return;
    }

    // 1. 서버에 주문 생성 요청 (POST 방식)
    fetch("${pageContext.request.contextPath}/order/prepare", {
        method: "POST",
        headers: {
            // CSRF 토큰을 헤더에 넣지 않고 body에 넣을 것이므로 Content-Type만 설정
            "Content-Type": "application/x-www-form-urlencoded"
        },
        // 2. body에 CSRF 토큰과 파라미터명을 함께 담음
        body: "${_csrf.parameterName}=${_csrf.token}"
    })
    .then(response => response.json())
    .then(data => {
        // 3. 주문 준비 완료 후 토스 결제창 호출
        const tossPayments = TossPayments("test_ck_GjLJoQ1aVZp0Bbwb0yl58w6KYe2R");
        tossPayments.requestPayment('카드', {
            amount: data.totalPrice,
            orderId: data.orderId,
            orderName: data.orderName,
            successUrl: 'https://marina-elastic-wilder.ngrok-free.dev/order/success',
            //successUrl: 'https://localhost:8888/order/success',
            failUrl: 'https://marina-elastic-wilder.ngrok-free.dev/order/fail'
            //failUrl: 'https://localhost:8888/order/fail'
        });
    })
    .catch(error => {
        console.error("Error:", error);
        alert("주문 준비 중 오류가 발생했습니다.");
    });
}

function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
    return "";
}

function updateGuestCartCookie(bookId, newCount) {
    var cartCookie = getCookie("guestCart");
    var cartList = [];
    
    if (cartCookie) {
        try {
            var decoded = decodeURIComponent(cartCookie);
            cartList = JSON.parse(decoded);
        } catch (e) {
            console.error("Cookie parse error", e);
            cartList = [];
        }
    }
    
    var isExist = false;
    for (var i = 0; i < cartList.length; i++) {
        if (cartList[i].bookId === bookId) {
            cartList[i].count = newCount;
            isExist = true;
            break;
        }
    }
    
    if (!isExist) {
        cartList.push({ bookId: bookId, count: newCount });
    }
    
    document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
    location.reload();
}

function removeGuestCartCookie(bookId) {
    var cartCookie = getCookie("guestCart");
    if (cartCookie) {
        try {
            var cartList = JSON.parse(decodeURIComponent(cartCookie));
            cartList = cartList.filter(function(item) {
                return item.bookId !== bookId;
            });
            document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
        } catch(e) {
            document.cookie = "guestCart=; path=/; max-age=0";
        }
        location.reload();
    }
}
</script>