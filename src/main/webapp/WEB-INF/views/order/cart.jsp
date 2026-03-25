<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.cart-container {
	width: 900px;
	margin: 0 auto;
	padding-bottom: 50px;
}

.cart-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-bottom: 2px solid #333;
	padding-bottom: 10px;
	margin-bottom: 20px;
}

.cart-item {
	display: flex;
	align-items: center;
	padding: 20px 0;
	border-bottom: 1px solid #eee;
}

.cart-img {
	width: 100px;
	height: 140px;
	object-fit: cover;
	box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
}

.cart-info {
	flex: 2;
	margin-left: 25px;
}

.cart-title {
	font-size: 17px;
	font-weight: bold;
	color: #000;
	text-decoration: none;
}

.cart-qty {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
}

.qty-input {
	width: 45px;
	text-align: center;
	margin: 0 5px;
	border: 1px solid #ddd;
	padding: 3px;
}

.cart-price {
	flex: 1;
	text-align: right;
	font-weight: bold;
	font-size: 16px;
	color: #479e10;
}

.cart-control {
	flex: 0.5;
	text-align: right;
}

.delete-btn {
	background: none;
	border: 1px solid #ccc;
	color: #888;
	padding: 5px 10px;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
}

.delete-btn:hover {
	background: #f5f5f5;
	color: #ff0000;
}

.total-area {
	background: #f9f9f9;
	padding: 30px;
	margin-top: 30px;
	border-radius: 8px;
	text-align: right;
}

.total-label {
	font-size: 18px;
	color: #666;
}

.total-amount {
	font-size: 26px;
	font-weight: bold;
	color: #479e10;
	margin-left: 15px;
}

.order-btn-area {
	margin-top: 20px;
	text-align: center;
}

.btn-buy {
	background: #479e10;
	color: white;
	padding: 15px 60px;
	font-size: 18px;
	font-weight: bold;
	border: none;
	border-radius: 5px;
	cursor: pointer;
}

.btn-buy:hover {
	background: #3d8a0d;
}
</style>

<div class="cart-container">
	<div class="cart-header">
		<h2>장바구니</h2>
		<span style="color: #666;">전체 <strong>${cartList.size()}</strong>권
		</span>
	</div>

	<c:choose>
		<c:when test="${empty cartList}">
			<div style="text-align: center; padding: 80px 0; color: #999;">
				<p style="font-size: 18px;">장바구니가 비어 있습니다.</p>
				<a href="${pageContext.request.contextPath}/book/list"
					style="color: #479e10;">도서 구경하러 가기</a>
			</div>
		</c:when>
		<c:otherwise>
			<c:set var="totalSum" value="0" />
			<c:forEach var="item" items="${cartList}">
				<div class="cart-item">
					<img src="${item.bookimage}" class="cart-img" alt="${item.title}">
					<div class="cart-info">
						<a
							href="${pageContext.request.contextPath}/book/view?id=${item.bookId}"
							class="cart-title">${item.title}</a>
						<div style="margin-top: 5px; color: #888; font-size: 13px;">
							단가:
							<fmt:formatNumber value="${item.price}" type="number" />
							원
						</div>
					</div>

					<div class="cart-qty">
						<form action="${pageContext.request.contextPath}/order/updateCart"
							method="post">
							<input type="hidden" name="cartId" value="${item.cartId}">
							<input type="number" name="count" value="${item.count}" min="1"
								class="qty-input">
							<button type="submit" style="font-size: 11px; padding: 2px 5px;">변경</button>
						</form>
					</div>

					<div class="cart-price">
						<fmt:formatNumber value="${item.price * item.count}" type="number" />
						원
					</div>

					<c:set var="totalSum"
						value="${totalSum + (item.price * item.count)}" />

					<div class="cart-control">
						<button class="delete-btn"
							onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/order/deleteCart?cartId=${item.cartId}'">
							삭제</button>
					</div>
				</div>
			</c:forEach>

			<div class="total-area">
				<span class="total-label">총 결제 예정 금액</span> <span
					class="total-amount"><fmt:formatNumber value="${totalSum}"
						type="number" />원</span>
			</div>

			<div class="order-btn-area">
				<button type="button"
					onclick="location.href='${pageContext.request.contextPath}/book/list'"
					style="background: #666; color: white; padding: 15px 30px; border: none; border-radius: 5px; cursor: pointer; margin-right: 10px;">
					계속 쇼핑하기</button>

				<button class="btn-buy"
					onclick="location.href='${pageContext.request.contextPath}/order/buy'">주문하기</button>
			</div>
		</c:otherwise>
	</c:choose>
</div>