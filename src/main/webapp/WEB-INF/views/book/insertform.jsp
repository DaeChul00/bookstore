<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 등록</title>
<%-- updateform.jsp의 스타일을 그대로 적용 --%>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; color: #333; }
    .form-container { width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; }
    .form-group { margin-bottom: 15px; text-align: left; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
    .form-group input, .form-group textarea { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
    textarea { resize: vertical; height: 120px; }
    
    .btn-area { text-align: center; margin-top: 20px; }
    .btn { padding: 10px 20px; cursor: pointer; border: none; border-radius: 4px; color: white; font-weight: bold; font-size: 14px; text-decoration: none; display: inline-block; }
    /* updateform의 초록색 버튼 스타일 적용 */
    .btn-submit { background-color: #479e10; }
    .btn-submit:hover { background-color: #3e8e0d; }
    /* updateform의 회색 취소 버튼 스타일 적용 */
    .btn-cancel { background-color: #666; }
    .btn-cancel:hover { background-color: #555; }
</style>
</head>
<body>

<%-- updateform과 동일한 컨테이너 구조 사용 --%>
<div class="form-container">
    <h2 style="text-align:center; margin-bottom: 15px;">📚 도서 등록</h2>
    <hr style="margin-bottom: 20px; border:0; border-top:1px solid #eee;">
    
    <form action="${pageContext.request.contextPath}/book/insert" method="post">
    
    <%-- updateform의 form-group 구조 적용 --%>
    <div class="form-group">
        <label>ISBN</label>
        <input type="text" name="isbn" placeholder="ISBN 번호 입력">
    </div>
    
    <div class="form-group">
        <label>도서 이미지 URL</label>
        <input type="url" name="bookimage" placeholder="https://example.com/book.jpg">
    </div>
    
    <div class="form-group">
        <label>제목</label>
        <input type="text" name="title" required>
    </div>
    
    <div class="form-group">
        <label>저자</label>
        <input type="text" name="author" required>
    </div>
    
    <div class="form-group">
        <label>출판사</label>
        <input type="text" name="publisher">
    </div>
    
    <div class="form-group">
        <label>출판일</label>
        <input type="date" name="publictiondate">
    </div>
    
    <div class="form-group">
        <label>가격</label>
        <input type="number" name="price" required>
    </div>
    
    <div class="form-group">
        <label>평점</label>
        <input type="number" step="0.1" name="rating" min="0" max="10">
    </div>
    
    <div class="form-group">
        <label>내용</label>
        <textarea name="content"></textarea>
    </div>
    
    <%-- updateform의 btn-area 구조 적용 --%>
    <div class="btn-area">
        <button type="submit" class="btn btn-submit">도서 등록</button>
        <%-- '초기화' 버튼 대신 '목록으로' 또는 '취소' 버튼으로 변경하여 updateform과 구성을 맞춤 --%>
        <a href="${pageContext.request.contextPath}/book/list" class="btn btn-cancel">취소</a>
    </div>
    
    </form>
</div>

</body>
</html>