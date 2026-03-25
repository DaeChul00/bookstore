<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도서 상세보기</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; color: #333; }
    .container { width: 900px; margin: 0 auto; }
    .detail-box { display: flex; margin-top: 30px; }
    .book-img { width: 250px; height: 350px; object-fit: cover; }
    .book-info { margin-left: 40px; }
    .title { font-size: 24px; font-weight: bold; }
    .meta { margin: 10px 0; color: #666; }
    .price { font-size: 20px; font-weight: bold; margin: 10px 0; }
    .content { margin-top: 20px; line-height: 1.6; }
    .btn-area { margin-top: 30px; }
    .btn { padding: 10px 15px; background-color: green; color: white; border: none; cursor: pointer; margin-right: 10px; }
</style>
</head>
<body>
<div class="container">
    <h2>도서 상세보기</h2><hr>
    <div class="detail-box">
        <img src="${bk.bookimage}" class="book-img" alt="${bk.title}">
        <div class="book-info">
            <div class="title">${bk.title}</div>
            <div class="meta">${bk.author} · ${bk.publisher} · ${bk.publictiondate}</div>
            <div class="price"><fmt:formatNumber value="${bk.price}" type="number"/>원</div>
            <div>평점 ★ ${bk.rating}</div>
            <div class="content">${bk.content}</div>
            <div class="btn-area">
			    <c:choose>
			        <%-- 1. 관리자인 경우: 수정, 삭제, 목록 버튼 --%>
			        <c:when test="${loginUser.role == 'ADMIN'}">
			            <button class="btn btn-admin" 
			                onclick="location.href='${pageContext.request.contextPath}/book/update?id=${bk.id}'">
			                도서 수정
			            </button>
			            
			            <%-- 삭제 버튼 추가 --%>
			            <button class="btn" style="background-color: #d9534f;" 
						    onclick="if(confirm('정말 삭제하시겠습니까?')) { location.href='${pageContext.request.contextPath}/book/delete?id=${bk.id}'; }">
						    도서 삭제
						</button>
			
			            <button class="btn btn-list" 
			                onclick="location.href='${pageContext.request.contextPath}/book/list'">
			                목록으로
			            </button>
			        </c:when>
			
			        <%-- 2. 일반 유저(또는 로그인 안 한 유저)인 경우: 장바구니, 구매, 목록 버튼 --%>
			        <c:otherwise>
			            <button class="btn btn-cart" onclick="addCart(${bk.id})">
			                장바구니 담기
			            </button>
			            <button class="btn btn-buy" onclick="buyNow(${bk.id})">
			                바로 구매
			            </button>
			            <button class="btn btn-list" 
			                onclick="location.href='${pageContext.request.contextPath}/book/list'">
			                목록으로
			            </button>
			        </c:otherwise>
			    </c:choose>
			</div>
			
			<%-- 버튼 동작을 위한 간단한 스크립트 추가 --%>
			<script>
			function addCart(id) {
			    // 장바구니 처리 로직 (나중에 구현)
			    alert("장바구니에 담겼습니다.");
			}
			
			function buyNow(id) {
			    // 구매 페이지 이동 로직 (나중에 구현)
			    confirm("구매 페이지로 이동하시겠습니까?");
			}
			</script>
        </div>
    </div>
</div>
</body>
</html>