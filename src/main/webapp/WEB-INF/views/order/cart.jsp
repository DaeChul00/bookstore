<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                                       onchange="changeCount(${item.bookId}, this.value)">
                            </td>
                            <td><fmt:formatNumber value="${item.price * item.count}" type="number"/>원</td>
                            <td>
                                <button onclick="deleteItem(${item.bookId})" style="background:#d9534f; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;">삭제</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
            <div class="total-area">
                <button class="btn-order" onclick="location.href='${pageContext.request.contextPath}/order/buy'">주문하기 ➡️</button>
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
function changeCount(bookId, count) {
    var form = document.getElementById("cartActionForm");
    form.action = "${pageContext.request.contextPath}/order/updateCart";
    document.getElementById("actionBookId").value = bookId;
    document.getElementById("actionCount").value = count;
    form.submit();
}

function deleteItem(bookId) {
    if(confirm("해당 상품을 장바구니에서 삭제하시겠습니까?")) {
        var form = document.getElementById("cartActionForm");
        form.action = "${pageContext.request.contextPath}/order/deleteCart";
        document.getElementById("actionBookId").value = bookId;
        form.submit();
    }
}
</script>