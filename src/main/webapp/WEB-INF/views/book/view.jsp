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
.btn-area { margin-top: 30px; display: flex; gap: 10px; }
.btn { padding: 12px 28px; border-radius: 6px; font-weight: bold; cursor: pointer; text-decoration: none; border: none; }
.btn-cart { background: #fff; border: 1px solid #ccc; color: #333; }
.btn-list { background: #2c3e50; color: white; }
.review-section { margin-top: 50px; border-top: 2px solid #2c3e50; padding-top: 30px; }
.review-item { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 15px; }
.review-top { display: flex; justify-content: space-between; margin-bottom: 8px; }
.review-user { font-weight: bold; color: #333; }
.review-stars { color: #f39c12; margin-left: 10px; }
.review-date { color: #999; font-size: 13px; }
.review-content { color: #555; line-height: 1.5; }
</style>

<div class="detail-container">
    <div class="detail-card">
        <img src="${bk.bookimage}" class="book-img">
        <div class="book-info">
            <div class="title">${bk.title}</div>
            <div class="meta">${bk.author} | ${bk.publisher} | ${bk.publictiondate}</div>
            <div class="price"><fmt:formatNumber value="${bk.price}" type="number"/>원</div>
            <div class="rating">
                <c:choose>
                    <c:when test="${bk.reviewCount == 0}">
                        <span style="color:#aaa; font-weight:normal;">💬 등록된 평점이 아직 없습니다.</span>
                    </c:when>
                    <c:otherwise>
                        ⭐ ${bk.avgRating}점 (${bk.reviewCount}개의 리뷰)
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="content">${bk.content}</div>
            
            <div class="btn-area">
                <button type="button" class="btn btn-cart" onclick="addCart(${bk.id})">🛒 장바구니 담기</button>
                <a href="${pageContext.request.contextPath}/book/list" class="btn btn-list">목록으로</a>
            </div>
        </div>
    </div>

    <div class="review-section">
        <h3 class="mb-4">💬 한줄평 리뷰 (${bk.reviewCount})</h3>
        <c:choose>
            <c:when test="${empty reviewList}">
                <div class="text-center py-5 text-muted">첫 번째 리뷰어가 되어 이 책을 추천해 주세요!</div>
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
    /
    location.href = "${pageContext.request.contextPath}/order/addCart?bookId=" + id + "&count=1";
}
</script>