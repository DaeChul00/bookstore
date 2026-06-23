<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.detail-container { width: 1000px; margin: 50px auto; }
.detail-card { display: flex; gap: 40px; background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05); }
.book-img { width: 280px; height: 380px; object-fit: cover; border-radius: 10px; }
.book-info { flex: 1; }
.title { font-size: 26px; font-weight: bold; color: #2c3e50; }
.meta { margin: 10px 0; color: #777; font-size: 14px; }
.price { font-size: 22px; font-weight: bold; color: #e67e22; margin: 15px 0; }
.rating { color: #f39c12; font-weight: bold; margin-bottom: 15px; }
.content { margin-top: 20px; line-height: 1.6; color: #555; }
.btn-area { margin-top: 30px; }
.btn { padding: 10px 18px; border-radius: 6px; border: none; font-weight: bold; cursor: pointer; text-decoration: none; font-size: 14px; margin-right: 10px; }
.btn-primary { background: #2c3e50; color: white; }
.btn-primary:hover { background: #1a252f; }
.btn-danger { background: #d9534f; color: white; }
.btn-warning { background: #e67e22; color: white; }
.btn-secondary { background: #aaa; color: white; }

/* 리뷰 섹션 스타일 */
.review-section { width: 100%; margin-top: 50px; border-top: 2px solid #2c3e50; padding-top: 30px; }
.review-header { font-size: 20px; font-weight: bold; margin-bottom: 20px; color: #2c3e50; }
.review-item { border-bottom: 1px solid #eee; padding: 15px 0; }
.review-top { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
.review-user { font-weight: bold; color: #333; }
.review-stars { color: #f39c12; font-weight: bold; }
.review-date { color: #999; font-size: 12px; }
.review-content { font-size: 14px; color: #555; line-height: 1.6; }
.no-review { text-align: center; padding: 40px 0; color: #aaa; font-size: 14px; }
</style>

<div class="detail-container">
	<div class="detail-card">
		<img src="${bk.bookimage}" class="book-img" alt="${bk.title}">

		<div class="book-info">
			<div class="title">${bk.title}</div>
			<div class="meta">${bk.author} · ${bk.publisher} · ${bk.publictiondate}</div>
			<div class="price">
				<fmt:formatNumber value="${bk.price}" type="number" />원
			</div>

			<div class="rating">
				<c:choose>
					<c:when test="${bk.reviewCount == 0}">
						<span style="color: #aaa; font-weight: normal; font-size: 12px;">💬 첫 리뷰를 기다려요</span>
					</c:when>
					<c:otherwise>
						★ ${bk.avgRating} <span style="color: #888; font-size: 12px; font-weight: normal;">(${bk.reviewCount})</span>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="content">${bk.content}</div>

			<div class="btn-area">
				<c:choose>
					<c:when test="${loginUser.role == 'ADMIN'}">
						<button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/book/update?id=${bk.id}'">수정</button>
						<button class="btn btn-danger" onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/book/delete?id=${bk.id}'">삭제</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-warning" onclick="addCart(${bk.id})">🛒 장바구니</button>
						<button class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/book/list'">목록</button>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
	
	<div class="review-section">
	    <div class="review-header">💬 한줄 평 / 리뷰 (${bk.reviewCount})</div>
	    <c:choose>
	        <c:when test="${empty reviewList}">
	            <div class="no-review">첫 번째 리뷰의 주인공이 되어보세요!</div>
	        </c:when>
	        <c:otherwise>
	            <c:forEach var="review" items="${reviewList}">
	                <div class="review-item">
	                    <div class="review-top">
	                        <div>
	                            <span class="review-user">${review.memberId}</span>
	                            <span class="review-stars">
	                                <c:choose>
	                                    <c:when test="${review.rating == 5}">★★★★★</c:when>
	                                    <c:when test="${review.rating == 4}">★★★★☆</c:when>
	                                    <c:when test="${review.rating == 3}">★★★☆☆</c:when>
	                                    <c:when test="${review.rating == 2}">★★☆☆☆</c:when>
	                                    <c:otherwise>★☆☆☆☆</c:otherwise>
                                    </c:choose>
	                                ${review.rating}점
	                            </span>
	                        </div>
	                        <span class="review-date">${review.regDate}</span>
	                    </div>
	                    <div class="review-content">${review.content}</div>
	                </div>
	            </c:forEach>
	        </c:otherwise>
	    </c:choose>
	</div>
</div>

<script>
function addCart(id) {
    <c:if test="${empty loginUser}">
        alert("로그인이 필요합니다.");
        location.href = "${pageContext.request.contextPath}/login";
        return;
    </c:if>
    location.href = "${pageContext.request.contextPath}/order/addCart?bookId=" + id + "&count=1";
}
</script>