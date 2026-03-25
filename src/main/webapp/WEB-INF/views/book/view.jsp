<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 상세보기</title>

<style>
    body { font-family: 'Malgun Gothic', sans-serif; color: #333; }
    .container { width: 900px; margin: 0 auto; }
    .detail-box { display: flex; margin-top: 30px; }
    .book-img { width: 250px; height: 350px; object-fit: cover; }
    .book-info { margin-left: 40px; }
    .title { font-size: 24px; font-weight: bold; }
    .meta { margin: 10px 0; color: #666; }
    .price { font-size: 20px; font-weight: bold; margin: 10px 0; }
    .content { margin-top: 20px; line-height: 1.6; }
    .btn-area { margin-top: 30px; }
    .btn {
        padding: 10px 15px;
        background-color: green;
        color: white;
        border: none;
        cursor: pointer;
        margin-right: 10px;
    }
</style>
</head>

<body>

<div class="container">

    <h2>도서 상세보기</h2>
    <hr>

    <div class="detail-box">
        <img src="${bk.bookimage}" class="book-img" alt="${bk.title}">

        <div class="book-info">
            <div class="title">${bk.title}</div>

            <div class="meta">
                ${bk.author} · ${bk.publisher} · ${bk.publictiondate}
            </div>

            <div class="price">
                <fmt:formatNumber value="${bk.price}" type="number"/>원
            </div>

            <div>
                평점 ★ ${bk.rating}
            </div>

            <div class="content">
                ${bk.content}
            </div>

            <div class="btn-area">
                <button class="btn"
                    onclick="location.href='${pageContext.request.contextPath}/book/updateform?id=${bk.id}'">
                    수정
                </button>

                <button class="btn"
                    onclick="location.href='${pageContext.request.contextPath}/book/list'">
                    목록
                </button>
            </div>
        </div>
    </div>

</div>

</body>
</html>