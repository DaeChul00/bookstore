<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h3>출판사별 도서 수</h3>
	<c:forEach var="stat" items="${publisherStats}">
    ${stat.PUBLISHER} : ${stat.COUNT}권<br>
	</c:forEach>

	<h3> 최근 매출 현황 (날짜별)</h3>
	<table border="1">
		<tr>
			<th>날짜</th>
			<th>총 매출액</th>
		</tr>
		<c:forEach var="sale" items="${dailySales}">
			<tr>
				<td>${sale.DATE}</td>
				<td><fmt:formatNumber value="${sale.TOTAL_SALES}" type="currency" /></td>
			</tr>
		</c:forEach>
	</table>
	
	<hr>
	
	<h3> 실시간 베스트셀러 (판매량 순)</h3>
	<ul>
		<c:forEach var="best" items="${bestSellers}">
			<li>${best.TITLE}— <b>${best.TOTAL_COUNT}권</b> 판매됨
			</li>
		</c:forEach>
	</ul>
	
	<h3>인기 도서 TOP 5 (평점순)</h3>
	<ul>
		<c:forEach var="book" items="${topBooks}">
			<li>${book.title}(평점:${book.rating})</li>
		</c:forEach>
	</ul>

</body>
</html>