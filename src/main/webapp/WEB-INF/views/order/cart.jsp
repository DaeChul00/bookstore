<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container" style="padding: 40px 20px;">
    <div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 30px; display: flex; align-items: center; gap: 10px;">
        <h2 style="margin:0; color:#2c3e50;">🛒 장바구니</h2>
    </div>

    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
        <thead style="background-color: #f8f9fa; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
            <tr style="height: 50px;">
                <th style="width: 50%;">상품정보</th>
                <th style="width: 15%;">수량</th>
                <th style="width: 20%;">금액</th>
                <th style="width: 15%;">관리</th>
            </tr>
        </thead>
        <tbody>
            <c:set var="totalSum" value="0" />
            <c:forEach var="item" items="${cartList}">
                <tr style="border-bottom: 1px solid #eee; height: 120px; text-align: center;">
                    <td style="text-align: left; padding: 20px; display: flex; align-items: center; gap: 20px;">
                        <img src="${item.bookimage}" style="width: 70px; height: 100px; object-fit: cover; border: 1px solid #ddd;">
                        <span style="font-weight: bold;">${item.title}</span>
                    </td>
                    <td>
                        <form action="${pageContext.request.contextPath}/order/updateCart" method="post" style="display: flex; justify-content: center; gap: 5px;">
                            <input type="hidden" name="cartId" value="${item.cartId}">
                            <input type="number" name="count" value="${item.count}" min="1" style="width: 50px; padding: 5px; border: 1px solid #ccc;">
                            <button type="submit" style="padding: 5px 10px; background: #2c3e50; color: white; border: none; cursor: pointer; font-size: 12px;">변경</button>
                        </form>
                    </td>
                    <td style="font-weight: bold; color: #e67e22;">
                        <fmt:formatNumber value="${item.price * item.count}" type="number"/>원
                    </td>
                    <td>
                        <button onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/order/deleteCart?cartId=${item.cartId}'"
                                style="padding: 5px 10px; background: #666; color: white; border: none; cursor: pointer;">삭제</button>
                    </td>
                </tr>
                <c:set var="totalSum" value="${totalSum + (item.price * item.count)}" />
            </c:forEach>
        </tbody>
    </table>

    <div style="text-align: right; padding: 20px; border-top: 2px solid #eee; margin-bottom: 40px;">
        <span style="font-size: 18px; color: #666;">총 결제 예정 금액 : </span>
        <span style="font-size: 28px; font-weight: bold; color: #2c3e50;">
            <fmt:formatNumber value="${totalSum}" type="number"/>원
        </span>
    </div>

    <div style="display: flex; justify-content: center; gap: 15px; margin-bottom: 60px;">
        <a href="${pageContext.request.contextPath}/book/list" 
           style="padding: 15px 40px; border: 1px solid #2c3e50; color: #2c3e50; text-decoration: none; font-weight: bold;">계속 쇼핑하기</a>
        <button onclick="location.href='${pageContext.request.contextPath}/order/buy'" 
                style="padding: 15px 60px; background: #2c3e50; color: white; border: none; font-weight: bold; cursor: pointer; font-size: 18px;">주문하기</button>
    </div>
</div>