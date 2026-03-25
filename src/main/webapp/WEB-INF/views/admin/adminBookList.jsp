<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container" style="margin-top: 40px;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2>📚 도서 통합 관리 (관리자)</h2>
        <a href="${pageContext.request.contextPath}/book/insertform\" class="btn btn-primary">새 도서 등록</a>
    </div>

    <table class="table table-hover" style="vertical-align: middle;">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>이미지</th>
                <th>도서명</th>
                <th>저자</th>
                <th>출판사</th>
                <th>가격</th>
                <th>평점</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="book" items="${bookList}">
                <tr>
                    <td>${book.id}</td>
                    <td><img src="${book.bookimage}" style="width: 50px; height: 70px; object-fit: cover;"></td>
                    <td><strong>${book.title}</strong></td>
                    <td>${book.author}</td>
                    <td>${book.publisher}</td>
                    <td><fmt:formatNumber value="${book.price}" pattern="#,###원"/></td>
                    <td>⭐ ${book.rating}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/book/updateform?id=${book.id}" class="btn btn-sm btn-outline-success">수정</a>
                        <button class="btn btn-sm btn-outline-danger" 
                                onclick="if(confirm('정말 삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/book/delete?id=${book.id}'">삭제</button>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>