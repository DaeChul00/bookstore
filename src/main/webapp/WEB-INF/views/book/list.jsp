<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${not empty message}">
<script>
    let kind = "${kind}"; 
    let message = "${message}";
    if(message === "success"){
        // 삭제(delete) 케이스 추가
        if(kind === "delete") alert("도서가 삭제되었습니다.");
        else alert(kind === "insert" ? "도서 등록 성공!" : "도서 수정 성공!");
    } else if(message === "fail") {
        alert("처리에 실패했습니다.");
    }
</script>
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 목록 - 교보문고 스타일</title>
<style>
    .book-container { width: 1000px; margin: 40px auto; }
    .headline { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px; }
    .book-item { display: flex; padding: 25px 0; border-bottom: 1px solid #eee; }
    .book-img { width: 160px; height: 230px; object-fit: cover; box-shadow: 3px 3px 10px rgba(0,0,0,0.1); border-radius: 4px; }
    .book-info { flex: 1; margin-left: 35px; }
    .book-title { font-size: 20px; font-weight: bold; color: #2c3e50; text-decoration: none; }
    .book-meta { margin: 10px 0; color: #777; font-size: 14px; }
    .price-area { margin: 12px 0; font-size: 18px; font-weight: bold; color: #e67e22; }
    .content-preview { color: #666; font-size: 14px; line-height: 1.6; height: 45px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; margin-bottom: 15px; }
    .rating-area { color: #f39c12; font-weight: bold; font-size: 15px; }
    .insertbtn { background-color: #2c3e50; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-weight: bold; text-decoration: none; }
</style>
</head>
<body>
<div class="book-container">
    <div class="headline">
    <h2 style="margin:0; color:#2c3e50;">전체 도서 목록</h2>
    
    <div class="search-area">
        <form action="${pageContext.request.contextPath}/book/list" method="get" style="display: flex; gap: 5px;">
            <select name="category" style="padding: 5px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="title">제목</option>
                <option value="author">저자</option>
                <option value="publisher">출판사</option>
            </select>
            <input type="text" name="keyword" placeholder="검색어를 입력하세요" 
                   style="padding: 5px 10px; border: 1px solid #ccc; border-radius: 4px; width: 200px;">
            <button type="submit" class="insertbtn" style="padding: 5px 15px;">검색</button>
        </form>
    </div>

    <c:if test="${loginUser.role == 'ADMIN'}">
        <a href="${pageContext.request.contextPath}/book/insert" class="insertbtn">도서 등록</a>
    </c:if>
</div>
    <c:forEach var="book" items="${list}">
        <div class="book-item">
            <div class="img-box"><a href="${pageContext.request.contextPath}/book/view?id=${book.id}"><img src="${book.bookimage}" class="book-img"></a></div>
            <div class="book-info">
                <a href="${pageContext.request.contextPath}/book/view?id=${book.id}" class="book-title">${book.title}</a>
                <div class="book-meta">${book.author} · ${book.publisher}</div>
                <div class="price-area"><fmt:formatNumber value="${book.price}" type="number"/>원</div>
                <div class="content-preview">${book.content}</div>
                <div class="rating-area">★ ${book.rating}</div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty list}">
		<div style="text-align: center; padding: 100px 0; color: #999;">
			<p style="font-size: 18px;">검색 결과와 일치하는 도서가 없습니다. 🔍</p>
			<a href="${pageContext.request.contextPath}/book/list" style="color: var(--kb-green);">전체 목록 보기</a>
		</div>
	</c:if>
</div>
</body>
</html>