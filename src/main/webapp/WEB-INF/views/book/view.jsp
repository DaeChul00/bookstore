<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
.detail-container {
    width: 1000px;
    margin: 50px auto;
}

/* 카드 */
.detail-card {
    display: flex;
    gap: 40px;
    background: #fff;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.05);
}

/* 이미지 */
.book-img {
    width: 280px;
    height: 380px;
    object-fit: cover;
    border-radius: 10px;
}

/* 오른쪽 정보 */
.book-info {
    flex: 1;
}

/* 제목 */
.title {
    font-size: 26px;
    font-weight: bold;
    color: #2c3e50;
}

/* 메타 */
.meta {
    margin: 10px 0;
    color: #777;
    font-size: 14px;
}

/* 가격 */
.price {
    font-size: 22px;
    font-weight: bold;
    color: #e67e22;
    margin: 15px 0;
}

/* 평점 */
.rating {
    color: #f39c12;
    font-weight: bold;
    margin-bottom: 15px;
}

/* 내용 */
.content {
    margin-top: 20px;
    line-height: 1.6;
    color: #555;
}

/* 버튼 영역 */
.btn-area {
    margin-top: 30px;
}

/* 버튼 */
.btn {
    padding: 10px 18px;
    border-radius: 6px;
    border: none;
    font-weight: bold;
    cursor: pointer;
    text-decoration: none;
    font-size: 14px;
    margin-right: 10px;
}

/* 버튼 색상 */
.btn-primary {
    background: #2c3e50;
    color: white;
}

.btn-primary:hover {
    background: #1a252f;
}

.btn-danger {
    background: #d9534f;
    color: white;
}

.btn-warning {
    background: #e67e22;
    color: white;
}

.btn-secondary {
    background: #aaa;
    color: white;
}
</style>

<div class="detail-container">

    <div class="detail-card">

        <!-- 이미지 -->
        <img src="${bk.bookimage}" class="book-img" alt="${bk.title}">

        <!-- 정보 -->
        <div class="book-info">

            <div class="title">${bk.title}</div>

            <div class="meta">
                ${bk.author} · ${bk.publisher} · ${bk.publictiondate}
            </div>

            <div class="price">
                <fmt:formatNumber value="${bk.price}" type="number"/>원
            </div>

            <div class="rating">★ ${bk.rating}</div>

            <div class="content">${bk.content}</div>

            <div class="btn-area">
                <c:choose>
                    <c:when test="${loginUser.role == 'ADMIN'}">
                        <button class="btn btn-primary"
                                onclick="location.href='${pageContext.request.contextPath}/book/update?id=${bk.id}'">
                            수정
                        </button>

                        <button class="btn btn-danger"
                                onclick="if(confirm('삭제하시겠습니까?')) location.href='${pageContext.request.contextPath}/book/delete?id=${bk.id}'">
                            삭제
                        </button>
                    </c:when>

                    <c:otherwise>
                        <button class="btn btn-warning"
                                onclick="addCart(${bk.id})">
                            🛒 장바구니
                        </button>

                        <button class="btn btn-secondary"
                                onclick="location.href='${pageContext.request.contextPath}/book/list'">
                            목록
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
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