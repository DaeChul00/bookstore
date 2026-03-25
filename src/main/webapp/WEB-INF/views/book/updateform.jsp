<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 정보 수정</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; }
    .form-container { width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; }
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
    .form-group input, .form-group textarea { width: 100%; padding: 8px; box-sizing: border-box; }
    .btn-area { text-align: center; margin-top: 20px; }
    .btn { padding: 10px 20px; cursor: pointer; border: none; border-radius: 4px; color: white; }
    .btn-submit { background-color: #479e10; }
    .btn-cancel { background-color: #666; text-decoration: none; display: inline-block; }
</style>
</head>
<body>
<div class="form-container">
    <h2>도서 정보 수정</h2>
    <hr>
    <form action="${pageContext.request.contextPath}/book/update" method="post">
    <input type="hidden" name="id" value="${bk.id}">

    <div class="form-group">
        <label>ISBN</label>
        <input type="text" name="isbn" value="${bk.isbn}">
    </div>

    <div class="form-group">
        <label>도서 제목</label>
        <input type="text" name="title" value="${bk.title}" required>
    </div>

    <div class="form-group">
        <label>저자</label>
        <input type="text" name="author" value="${bk.author}">
    </div>

    <div class="form-group">
        <label>출판사</label>
        <input type="text" name="publisher" value="${bk.publisher}">
    </div>

    <div class="form-group">
        <label>출판일</label>
        <input type="text" name="publictiondate" value="${bk.publictiondate}">
    </div>

    <div class="form-group">
        <label>가격</label>
        <input type="number" name="price" value="${bk.price}">
    </div>

    <div class="form-group">
        <label>평점</label>
        <input type="number" step="0.1" name="rating" value="${bk.rating}">
    </div>

    <div class="form-group">
        <label>이미지 경로</label>
        <input type="text" name="bookimage" value="${bk.bookimage}">
    </div>

    <div class="form-group">
        <label>도서 설명</label>
        <textarea name="content" style="height:100px;">${bk.content}</textarea>
    </div>

    <div class="btn-area">
        <button type="submit" class="btn btn-submit">수정 완료</button>
        <a href="${pageContext.request.contextPath}/book/view?id=${bk.id}" class="btn btn-cancel">취소</a>
    </div>
</form>
</div>
</body>
</html>