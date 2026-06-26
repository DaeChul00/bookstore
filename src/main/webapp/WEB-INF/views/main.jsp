<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.main-container { width: 1100px; margin: 40px auto; }
.section { margin-bottom: 50px; }
.section h2 { border-left: 5px solid #005a32; padding-left: 10px; margin-bottom: 0; color: #2c3e50; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.more-link { font-size: 14px; color: #767676; text-decoration: none; font-weight: bold; transition: 0.2s; }
.more-link:hover { color: #005a32; text-decoration: underline; }
.book-list { display: flex; gap: 20px; flex-wrap: wrap; }
.book-card { width: 200px; border-radius: 10px; overflow: hidden; transition: 0.3s; cursor: pointer; }
.book-card:hover { transform: translateY(-5px); }
.book-card img { width: 100%; height: 260px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.book-info { margin-top: 10px; }
.book-title { font-weight: bold; font-size: 14px; color: #2c3e50; text-decoration: none; display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.book-meta { margin: 4px 0; color: #777; font-size: 12px; }
.rating { color: #f39c12; font-weight: bold; font-size: 13px; }
</style>

<div class="main-container">

    <div class="section">
        <div class="section-header">
            <h2>🔥 추천 도서</h2>
            <a href="${pageContext.request.contextPath}/book/list?category=title&keyword=" class="more-link">더보기 →</a>
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
                        <div class="rating">
                            <c:choose>
                                <c:when test="${book.reviewCount == 0}">
                                    <span style="color: #aaa; font-weight: normal; font-size: 12px;">💬 첫 리뷰를 기다려요</span>
                                </c:when>
                                <c:otherwise>
                                    ★ ${book.avgRating} <span style="color: #888; font-size: 12px; font-weight: normal;">(${book.reviewCount})</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="section">
        <div class="section-header">
            <h2>🆕 신간 도서</h2>
            <a href="${pageContext.request.contextPath}/book/list" class="more-link">더보기 →</a>
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
                        <div class="rating">
                            <c:choose>
                                <c:when test="${book.reviewCount == 0}">
                                    <span style="color: #aaa; font-weight: normal; font-size: 12px;">💬 첫 리뷰를 기다려요</span>
                                </c:when>
                                <c:otherwise>
                                    ★ ${book.avgRating} <span style="color: #888; font-size: 12px; font-weight: normal;">(${book.reviewCount})</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>