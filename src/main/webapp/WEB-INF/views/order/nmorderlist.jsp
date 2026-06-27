<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container" style="padding: 40px 0; width: 1100px; margin: 0 auto;">
	<div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px;">
		<h2 style="margin: 0; color: #2c3e50;">🔍 비회원 주문 조회 결과</h2>
		<p style="font-size: 13px; color: #888; margin-top: 5px;">입력하신 정보로 조회된 주문 내역입니다.</p>
	</div>

	<table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
		<thead style="background-color: #f8f9fa; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
			<tr style="height: 50px; text-align: center; font-size: 14px;">
				<th style="width: 12%;">이미지</th>
				<th style="width: 33%;">상품 정보</th>
				<th style="width: 10%;">수량</th>
				<th style="width: 15%;">결제 금액</th>
				<th style="width: 15%;">주문 일자</th>
				<th style="width: 15%;">배송 상태</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<%-- 데이터가 없을 때 --%>
				<c:when test="${empty orderList}">
					<tr style="height: 150px; text-align: center;">
						<td colspan="6" style="color: #999;">조회된 비회원 주문 내역이 존재하지 않습니다.</td>
					</tr>
				</c:when>
				<%-- 데이터가 존재할 때 --%>
				<c:otherwise>
					<c:forEach var="order" items="${orderList}">
						<tr style="height: 120px; text-align: center; border-bottom: 1px solid #eee;">
							<td><img src="${order.bookimage}" style="width: 60px; height: 85px; object-fit: cover; border-radius: 4px; box-shadow: 1px 1px 5px rgba(0,0,0,0.1);"></td>
							
							<td style="text-align: left; padding-left: 20px;">
								<a href="${pageContext.request.contextPath}/order/detail?orderId=${order.orderId}" style="font-weight: bold; color: #2c3e50; text-decoration: none; display:block; margin-bottom:5px;">${order.title}</a>
								<small><a href="${pageContext.request.contextPath}/book/view?id=${order.bookId}" style="color:#777; text-decoration:none;">📖 도서 정보 상세보기</a></small>
							</td>
							
							<td><strong>${order.count}개</strong></td>
							
							<td><span style="color: #e67e22; font-weight: bold;"><fmt:formatNumber value="${order.orderPrice}" type="number"/>원</span></td>
							
							<td style="font-size: 13px; color: #666;">${order.orderDate}</td>
							
							<td>
								<div class="mb-2">
									<span id="status-${order.orderId}" class="badge ${order.deliveryStatus eq '배송완료' ? 'bg-success' : (order.deliveryStatus eq '배송중' ? 'bg-info text-white' : 'bg-secondary')}" style="font-size: 12px; padding: 5px 10px; display:inline-block;">
										${order.deliveryStatus}
									</span>
								</div>
								<small style="color: #aaa; font-size: 11px;">리뷰는 회원전용입니다</small>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>

	<div style="text-align: center; margin-top: 20px;">
		<a href="${pageContext.request.contextPath}/book" class="btn btn-dark" style="padding: 10px 25px; font-weight: bold; text-decoration: none;">메인으로 돌아가기</a>
	</div>
</div>

<script>
$(document).ready(function() {
	// 5초마다 배송 상태 비동기 갱신 백그라운드 엔진 가동 (기존 로직 유지)
	setInterval(function() {
		document.querySelectorAll('[id^="status-"]').forEach(el => {
			const orderId = el.id.split('-')[1];
			fetch('${pageContext.request.contextPath}/order/status?orderId=' + orderId)
			.then(response => response.text())
			.then(data => {
				if (el.innerText.trim() !== data.trim()) {
					el.innerText = data.trim();
					// 상태별 클래스 동적 변환
					if(data.trim() === '배송완료') {
						el.className = "badge bg-success";
					} else if(data.trim() === '배송중') {
						el.className = "badge bg-info text-white";
					} else {
						el.className = "badge bg-secondary";
					}
				}
			})
			.catch(err => console.error(err));
		});
	}, 5000);
});
</script>