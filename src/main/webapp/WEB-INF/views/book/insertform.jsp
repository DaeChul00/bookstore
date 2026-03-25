<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>도서 등록</title>

<style>

body{
    background:#f5f6fa;
    font-family:'Segoe UI', Arial;
}

.container{
    width:520px;
    margin:60px auto;
    background:white;
    padding:35px;
    border-radius:12px;
    box-shadow:0 8px 20px rgba(0,0,0,0.08);
}

h2{
    text-align:center;
    margin-bottom:25px;
    color:#333;
}

label{
    font-weight:600;
    margin-top:14px;
    display:block;
    color:#444;
}

input, textarea{
    width:100%;
    padding:10px;
    margin-top:6px;
    border:1px solid #ddd;
    border-radius:6px;
    font-size:14px;
    transition:0.2s;
}

input:focus, textarea:focus{
    border-color:#4CAF50;
    outline:none;
    box-shadow:0 0 4px rgba(76,175,80,0.4);
}

textarea{
    resize:vertical;
    height:120px;
}

.btn-group{
    margin-top:25px;
    text-align:center;
}

button{
    padding:10px 22px;
    border:none;
    border-radius:6px;
    font-size:15px;
    cursor:pointer;
    transition:0.2s;
}

.submit-btn{
    background:#4CAF50;
    color:white;
}

.submit-btn:hover{
    background:#43a047;
}

.reset-btn{
    background:#e0e0e0;
}

.reset-btn:hover{
    background:#cfcfcf;
}

</style>
</head>

<body>

<div class="container">

<h2>📚 도서 등록</h2>

<form action="${pageContext.request.contextPath}/book/insert" method="post">
      
<label>ISBN</label>
<input type="text" name="isbn" placeholder="ISBN 번호 입력">

<label>도서 이미지 URL</label>
<input type="url" name="bookimage" placeholder="https://example.com/book.jpg">

<label>제목</label>
<input type="text" name="title" placeholder="책 제목 입력" required>

<label>저자</label>
<input type="text" name="author" placeholder="저자 이름 입력" required>

<label>출판사</label>
<input type="text" name="publisher" placeholder="출판사 입력">

<label>출판일</label>
<input type="date" name="publictiondate"> <!-- 🔥 여기 수정 -->

<label>가격</label>
<input type="number" name="price" placeholder="가격 입력" min="0" required>

<label>내용</label>
<textarea name="content" placeholder="도서 설명 입력"></textarea>

<label>평점</label>
<input type="number" name="rating" step="0.1" min="0" max="10">

<div class="btn-group">
<button type="submit" class="submit-btn">
도서 등록
</button>
			
<button type="reset" class="reset-btn">초기화</button>

<button type="button" onclick="history.back()">목록으로 돌아가기</button> <!-- 🔥 수정 -->
</div>

</form>

</div>

</body>
</html>