<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.main-container {
    width: 1100px;
    margin: 40px auto;
}

/* 섹션 */
.section {
    margin-bottom: 50px;
}

.section h2 {
    border-left: 5px solid #2c3e50;
    padding-left: 10px;
    margin-bottom: 20px;
}

/* 카드 리스트 */
.book-list {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}

/* 카드 */
.book-card {
    width: 200px;
    border-radius: 10px;
    overflow: hidden;
    transition: 0.3s;
    cursor: pointer;
}

.book-card:hover {
    transform: translateY(-5px);
}

/* 이미지 */
.book-card img {
    width: 100%;
    height: 260px;
    object-fit: cover;
    border-radius: 8px;
}

/* 정보 */
.book-info {
    margin-top: 10px;
}

.book-title {
    font-weight: bold;
    font-size: 14px;
    color: #2c3e50;
    text-decoration: none;
}

.book-meta {
    font-size: 12px;
    color: #777;
}

.rating {
    color: #f39c12;
    font-weight: bold;
}
</style>

<div class="main-container">

    <!-- ⭐ 평점 높은 도서 -->
    <div class="section">
    <div style="display:flex; justify-content:space-between; align-items:center;">
        <h2>⭐ 평점 높은 도서</h2>
        <a href="${pageContext.request.contextPath}/book/list?category=title"
           style="font-size:14px; color:#2c3e50; text-decoration:none;">
            더보기 →
        </a>
        </div>
        <div class="book-list">
            <c:forEach var="book" items="${topRatedList}">
                <div class="book-card">
                    <a href="${pageContext.request.contextPath}/book/view?id=${book.id}">
                        <img src="${book.bookimage}">
                    </a>
                    <div class="book-info">
                        <a href="${pageContext.request.contextPath}/book/view?id=${book.id}" class="book-title">
                            ${book.title}
                        </a>
                        <div class="book-meta">${book.author}</div>
                        <div class="rating">★ ${book.rating}</div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- 🆕 신간 도서 -->
    <div class="section">
    <div style="display:flex; justify-content:space-between; align-items:center;">
        <h2>🆕 신간 도서</h2>
        <a href="${pageContext.request.contextPath}/book/list"
           style="font-size:14px; color:#2c3e50; text-decoration:none;">
            더보기 →
        </a>
        </div>
        <div class="book-list">
            <c:forEach var="book" items="${newBookList}">
                <div class="book-card">
                    <a href="${pageContext.request.contextPath}/book/view?id=${book.id}">
                        <img src="${book.bookimage}">
                    </a>
                    <div class="book-info">
                        <a href="${pageContext.request.contextPath}/book/view?id=${book.id}" class="book-title">
                            ${book.title}
                        </a>
                        <div class="book-meta">${book.author}</div>
                        <div class="rating">★ ${book.rating}</div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

</div>