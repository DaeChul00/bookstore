<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .order-container { width: 1000px; margin: 40px auto; padding: 30px; background: #fff; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
    .order-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px; }
    
    /* 테이블 스타일 */
    .order-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
    .order-table th { background-color: #f8f9fa; padding: 15px; border-bottom: 1px solid #dee2e6; text-align: center; color: #495057; }
    .order-table td { padding: 15px; border-bottom: 1px solid #eee; vertical-align: middle; text-align: center; }
    
    .order-img { width: 80px; height: 110px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd; }
    .order-title { font-weight: bold; color: #333; text-align: left; }
    .order-date { font-size: 0.9em; color: #888; }
    
    /* 버튼 스타일 */
    .btn-area { text-align: center; margin-top: 20px; }
    .btn-home { 
        display: inline-block; padding: 12px 30px; background-color: #28a745; color: white; 
        text-decoration: none; border-radius: 5px; font-weight: bold; transition: 0.3s;
    }
    .btn-home:hover { background-color: #218838; color: white; }
    
    .empty-msg { text-align: center; padding: 50px; color: #999; }
</style>

<div class="order-container">
    <div class="order-header">
        <h2>📜 나의 주문 내역</h2>
        <span class="order-date">최근 주문한 순서대로 표시됩니다.</span>
    </div>
    
    <c:choose>
        <c:when test="${empty orderList}">
            <div class="empty-msg">
                <p>주문 내역이 없습니다.</p>
            </div>
        </c:when>
        <c:otherwise>
            <table class="order-table">
                <thead>
                    <tr>
                        <th>이미지</th>
                        <th>상품 정보</th>
                        <th>수량</th>
                        <th>결제 금액</th>
                        <th>주문 일자</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orderList}">
                        <tr>
                            <td><img src="${order.bookimage}" class="order-img"></td>
                            <td class="order-title">${order.title}</td>
                            <td>${order.count}권</td>
                            <td>
                                <strong style="color: #d9534f;">
                                    <fmt:formatNumber value="${order.orderPrice * order.count}" type="number"/>원
                                </strong>
                            </td>
                            <td class="order-date">${order.orderDate}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="btn-area">
        <a href="${pageContext.request.contextPath}/book/list" class="btn-home">계속 쇼핑하기 (메인으로)</a>
    </div>
</div>