<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container bg-white p-5 rounded shadow-sm" style="margin-top: 30px; margin-bottom: 50px;">
    <div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px;">
        <h2 style="margin:0; color:#2c3e50;">📦 [관리자] 전체 주문 및 배송 관리</h2>
        <p style="font-size: 13px; color: #888; margin-top: 5px;">모든 회원의 결제 내역과 배송 상태를 일괄 관리합니다.</p>
    </div>

    <table class="table table-hover align-middle text-center border-top">
        <thead class="table-light">
            <tr>
                <th style="width: 80px;">주문번호</th>
                <th style="width: 120px;">주문자 ID</th>
                <th style="width: 100px;">이미지</th>
                <th>상품 정보</th>
                <th style="width: 80px;">수량</th>
                <th style="width: 130px;">결제 금액</th>
                <th style="width: 150px;">주문 일자</th>
                <th style="width: 180px;">배송 상태 변경</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty adminOrderList}">
                <tr>
                    <td colspan="8" style="padding: 40px; color: #999;">접수된 회원 주문 내역이 없습니다.</td>
                </tr>
            </c:if>
            <c:forEach var="item" items="${adminOrderList}">
                <tr>
                    <td><strong>${item.orderId}</strong></td>
                    <td><span class="badge bg-secondary">${item.memberId}</span></td>
                    <td>
                        <img src="${item.bookimage}" style="width: 50px; height: 70px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px;">
                    </td>
                    <td class="text-start" style="font-weight: bold; padding-left: 15px;">${item.title}</td>
                    <td>${item.count}권</td>
                    <td>
                        <span style="color: #d9534f; font-weight: bold;">
                            <fmt:formatNumber value="${item.orderPrice}" type="number"/>원
                        </span>
                    </td>
                    <td style="font-size: 13px; color: #666;">${item.orderDate}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display: flex; gap: 5px; justify-content: center;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="hidden" name="orderId" value="${item.orderId}">
                            <select name="deliveryStatus" class="form-select form-select-sm" style="width: 120px; font-size: 13px;">
                                <option value="주문완료" <c:if test="${empty item.deliveryStatus || item.deliveryStatus eq '주문완료' || item.deliveryStatus eq '결제완료'}">selected</c:if>>주문완료</option>
                                <option value="배송준비" <c:if test="${item.deliveryStatus eq '배송준비'}">selected</c:if>>배송준비</option>
                                <option value="배송중" <c:if test="${item.deliveryStatus eq '배송중'}">selected</c:if>>배송중</option>
                                <option value="배송완료" <c:if test="${item.deliveryStatus eq '배송완료'}">selected</c:if>>배송완료</option>
                            </select>
                            <button type="submit" class="btn btn-sm btn-dark" style="font-size: 12px; padding: 2px 8px;">변경</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>