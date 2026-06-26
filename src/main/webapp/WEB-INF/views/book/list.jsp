<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
.book-container { width: 1000px; margin: 40px auto; }
.headline { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px; }
.book-item { display: flex; padding: 25px 0; border-bottom: 1px solid #eee; }
.book-img { width: 160px; height: 230px; object-fit: cover; box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.1); border-radius: 4px; }
.book-info { flex: 1; margin-left: 35px; }
.book-title { font-size: 20px; font-weight: bold; color: #2c3e50; text-decoration: none; }
.book-meta { margin: 10px 0; color: #777; font-size: 14px; }
.price-area { font-size: 18px; font-weight: bold; color: #e67e22; margin: 10px 0; }
.content-preview { color: #666; font-size: 14px; line-height: 1.6; height: 75px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; margin-bottom: 10px; }
.rating-area { color: #f39c12; font-weight: bold; font-size: 15px; }
.pagination-wrapper { margin-top: 40px; display: flex; justify-content: center; }
.pagination { display: flex; list-style: none; padding: 0; gap: 5px; }
.pagination li a { color: #333; text-decoration: none; padding: 8px 14px; border: 1px solid #ddd; border-radius: 4px; }
.pagination li.active a { background-color: #2c3e50; color: white; border-color: #2c3e50; }
.pagination li.disabled a { color: #ccc; pointer-events: none; background-color: #fafafa; }
</style>

<div class="book-container">
	<div class="headline">
		<h2>📚 도서 목록</h2>
		<sec:authorize access="hasRole('ADMIN')">
			<a href="${pageContext.request.contextPath}/book/insertform" class="btn btn-sm btn-outline-dark">➕ 새 도서 추가</a>
		</sec:authorize>
	</div>

	<c:forEach var="book" items="${p.list}">
		<div class="book-item">
			<a href="${pageContext.request.contextPath}/book/view?id=${book.id}">
				<img src="${book.bookimage}" class="book-img" alt="도서 이미지">
			</a>
			<div class="book-info">
				<a href="${pageContext.request.contextPath}/book/view?id=${book.id}" class="book-title">${book.title}</a>
				<div class="book-meta">${book.author} · ${book.publisher} | ${book.publictiondate}</div>
				
				<div class="price-area">
					<fmt:formatNumber value="${book.price}" type="number" />원
				</div>
				
				<div class="content-preview">${book.content}</div>
				
				<div class="rating-area">
					<c:choose>
						<c:when test="${book.reviewCount == 0}">
							<span style="color: #aaa; font-weight: normal; font-size: 13px;">💬 첫 한줄평을 남겨보세요!</span>
						</c:when>
						<c:otherwise>
							★ ${book.avgRating}점 <span style="color: #888; font-size: 13px; font-weight: normal;">(${book.reviewCount}개의 리뷰)</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</c:forEach>

	<div class="pagination-wrapper">
		<ul class="pagination">

			<%-- 1. [이전] 블록 가기 --% border-안전화 %>
			<c:choose>
				<c:when test="${p.pre}">
					<li><a href="${pageContext.request.contextPath}/book/list?page=${p.startPage - 1}&category=${category}&keyword=${keyword}">&laquo; 이전</a></li>
				</c:when>
				<c:otherwise>
					<li class="disabled"><a href="#">&laquo; 이전</a></li>
				</c:otherwise>
			</c:choose>

			<%-- 2. 10개 단위 페이지 번호 출력 --%>
			<c:forEach var="idx" begin="${p.startPage}" end="${p.endPage}">
				<li class="${p.requestPage == idx ? 'active' : ''}">
					<a href="${pageContext.request.contextPath}/book/list?page=${idx}&category=${category}&keyword=${keyword}">${idx}</a>
				</li>
			</c:forEach>

			<%-- 3. [다음] 블록 가기 --%>
			<c:choose>
				<c:when test="${p.next}">
					<li><a href="${pageContext.request.contextPath}/book/list?page=${p.endPage + 1}&category=${category}&keyword=${keyword}">다음 &raquo;</a></li>
				</c:when>
				<c:otherwise>
					<li class="disabled"><a href="#">다음 &raquo;</a></li>
				</c:otherwise>
			</c:choose>

		</ul>
	</div>
</div>