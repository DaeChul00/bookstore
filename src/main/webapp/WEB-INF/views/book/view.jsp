<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 상세보기</title>

<style>
body{
    font-family: Arial;
    background-color:#f5f5f5;
}

.container{
    width:800px;
    margin:50px auto;
    background:white;
    padding:30px;
    border-radius:10px;
    box-shadow:0 0 10px rgba(0,0,0,0.1);
}

.book-img{
    width:200px;
}

table{
    width:100%;
    border-collapse: collapse;
}

td{
    padding:10px;
    border-bottom:1px solid #ddd;
}

.title{
    font-size:24px;
    font-weight:bold;
}

.btn{
    margin-top:30px;
    padding:10px 20px;
    background:#333;
    color:white;
    border:none;
    border-radius:5px;
    cursor:pointer;
}

.btn:hover{
    background:#555;
}
</style>

</head>
<body>

<div class="container">

<table>

<tr>
<td rowspan="6" style="width:220px;">
<img class="book-img" src="${book.bookimage}">
</td>
<td class="title">${book.title}</td>
</tr>

<tr>
<td>저자 : ${book.author}</td>
</tr>

<tr>
<td>출판사 : ${book.publisher}</td>
</tr>

<tr>
<td>출판일 : ${book.publictiondate}</td>
</tr>

<tr>
<td>가격 : ${book.price} 원</td>
</tr>

<tr>
<td>평점 : ${book.rating}</td>
</tr>

<tr>
<td colspan="2">
<h3>책 소개</h3>
<p>${book.content}</p>
</td>
</tr>

</table>

<!-- 뒤로가기 버튼 -->
<button class="btn" onclick="location.href='${pageContext.request.contextPath}/book/updateform'">수정</button>
<button class="btn" onclick="history.back()">목록으로 돌아가기</button>

</div>

</body>
</html>