<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container" style="padding: 40px 0;">
    <div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px;">
        <h2 style="margin:0; color:#2c3e50;">📜 나의 주문 내역</h2>
        <p style="font-size: 13px; color: #888; margin-top: 5px;">최근 주문한 순서대로 표시됩니다.</p>
    </div>

    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
        <thead style="background-color: #f8f9fa; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
            <tr style="height: 50px; text-align: center; font-size: 14px;">
                <th style="width: 15%;">이미지</th>
                <th style="width: 40%;">상품 정보</th>
                <th style="width: 10%;">수량</th>
                <th style="width: 15%;">결제 금액</th>
                <th style="width: 20%;">주문 일자</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty orderList}">
                    <tr>
                        <td colspan="5" style="padding: 100px 0; text-align: center; color: #999;">주문 내역이 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="order" items="${orderList}">
                        <tr style="border-bottom: 1px solid #eee; text-align: center;">
                            <td style="padding: 15px;">
                                <img src="${order.bookimage}" style="width: 80px; height: 110px; object-fit: cover; border: 1px solid #ddd;">
                            </td>
                            <td style="text-align: left; padding: 15px; font-weight: bold;">${order.title}</td>
                            <td>${order.count}권</td>
                            <td>
                                <strong style="color: #d9534f;">
                                    <fmt:formatNumber value="${order.orderPrice * order.count}" type="number"/>원
                                </strong>
                            </td>
                            <td style="font-size: 13px; color: #666;">${order.orderDate}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 40px;">
        <a href="${pageContext.request.contextPath}/book/list" 
           style="padding: 12px 40px; border: 1px solid #2c3e50; color: #2c3e50; text-decoration: none; font-weight: bold; display: inline-block;">
           계속 쇼핑하기
        </a>
    </div>
</div>