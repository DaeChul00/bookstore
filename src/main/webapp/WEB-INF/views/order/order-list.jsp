<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container" style="padding: 40px 0; width: 1100px; margin: 0 auto;">
    <div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px;">
        <h2 style="margin:0; color:#2c3e50;">📜 나의 주문 내역</h2>
        <p style="font-size: 13px; color: #888; margin-top: 5px;">최근 주문한 순서대로 표시됩니다.</p>
    </div>

    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
        <thead style="background-color: #f8f9fa; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
            <tr style="height: 50px; text-align: center; font-size: 14px;">
                <th style="width: 15%;">이미지</th>
                <th style="width: 30%;">상품 정보</th>
                <th style="width: 10%;">수량</th>
                <th style="width: 15%;">결제 금액</th>
                <th style="width: 20%;">주문 일자</th>
                <th style="width: 10%;">리뷰</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty orderList}">
                    <tr>
                        <td colspan="6" style="padding: 100px 0; text-align: center; color: #999;">주문 내역이 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="order" items="${orderList}">
                        <tr style="border-bottom: 1px solid #eee; text-align: center;">
                            <td style="padding: 15px;">
                                <img src="${order.bookimage}" style="width: 80px; height: 110px; object-fit: cover; border: 1px solid #ddd;">
                            </td>
                            <td style="text-align: left; padding: 15px; font-weight: bold;">${order.title}</td>
                            <td>${order.count}권</td>
                            <td>
                                <strong style="color: #d9534f;">
                                    <%-- DAO에서 단가*수량을 이미 합산해서 TOTAL_PRICE로 던져주므로 바로 출력하면 됩니다 --%>
                                    <fmt:formatNumber value="${order.orderPrice}" type="number"/>원
                                </strong>
                            </td>
                            <td style="font-size: 13px; color: #666;">${order.orderDate}</td>
                            <td style="text-align:center;"> 
                                <a href="#" class="review-btn" 
                                   onclick="openReviewModal('${order.bookId}', '${order.title}'); return false;" 
                                   style="color: #2c3e50; font-weight: bold; text-decoration: none;">리뷰 작성</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 40px;">
        <a href="${pageContext.request.contextPath}/book/list" 
           style="padding: 12px 40px; border: 1px solid #2c3e50; color: #2c3e50; text-decoration: none; font-weight: bold; display: inline-block;">
           계속 쇼핑하기
        </a>
    </div>
    
    <div id="reviewModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background:#fff; padding:30px; border:1px solid #ddd; box-shadow:0 4px 15px rgba(0,0,0,0.2); z-index:1000; width:440px; border-radius:10px;">
        <h3 id="modalBookTitle" style="margin-top:0; color:#2c3e50; font-size: 18px; line-height: 1.4;">책 제목 위치</h3>
        
        <form id="reviewForm">
            <input type="hidden" name="bookId" id="modalBookId">
            
            <div style="margin-bottom:15px;">
                <label style="display:block; margin-bottom:5px; font-weight:bold; font-size: 14px;">평점</label>
                <select name="rating" style="width:100%; padding:8px; border-radius:4px; border:1px solid #ddd; outline: none;">
                    <option value="5">⭐⭐⭐⭐⭐ (5점)</option>
                    <option value="4">⭐⭐⭐⭐ (4점)</option>
                    <option value="3">⭐⭐⭐ (3점)</option>
                    <option value="2">⭐⭐ (2점)</option>
                    <option value="1">⭐ (1점)</option>
                </select>
            </div>
            
            <div style="margin-bottom:20px;">
                <label style="display:block; margin-bottom:5px; font-weight:bold; font-size: 14px;">리뷰 내용</label>
                <textarea name="content" rows="5" style="width:100%; padding:10px; box-sizing:border-box; border-radius:4px; border:1px solid #ddd; resize:none; outline: none;" placeholder="최소 10자 이상 작성해주세요."></textarea>
            </div>
            
            <div style="display:flex; gap:10px; justify-content:flex-end;">
                <button type="button" id="closeModalBtn" style="background:#ddd; border:none; padding:8px 15px; border-radius:4px; cursor:pointer; font-weight: bold;">취소</button>
                <button type="submit" style="background:#2c3e50; color:#fff; border:none; padding:8px 15px; border-radius:4px; cursor:pointer; font-weight: bold;">등록하기</button>
            </div>
        </form>
    </div>

    <div id="modalBg" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.4); z-index:999;"></div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
// [리뷰 작성] 클릭 시 인자들을 가로채 데이터 유실 상태를 최종 체크하고 바인딩하는 함수
function openReviewModal(bookId, bookTitle) {
    console.log("JSP 클릭 시점에서 실시간 포착된 bookId: ", bookId);
    
    // 데이터 유실 및 0 값을 브라우저 단에서 1차 차단
    if(!bookId || bookId === '0') {
        alert("도서 고유 번호(bookId) 누락 상태입니다. OrderDAO의 빌더 세팅을 다시 점검하세요.");
        return;
    }
    
    $("#modalBookId").val(bookId);
    $("#modalBookTitle").text("✍️ [" + bookTitle + "] 리뷰 작성");
    
    $("#reviewModal, #modalBg").show();
}

$(document).ready(function() {
    // 모달 닫기
    $("#closeModalBtn, #modalBg").on("click", function() {
        $("#reviewModal, #modalBg").hide();
        $("#reviewForm")[0].reset(); 
    });
    
    // 리뷰 등록 처리 비동기(Ajax) 요청
    $("#reviewForm").on("submit", function(e) {
        e.preventDefault();
        
        let content = $("textarea[name='content']").val();
        if(content.trim().length < 10) {
            alert("리뷰 내용을 10자 이상 적어주세요.");
            return;
        }
        
        let formData = $(this).serialize();
        
        $.ajax({
            url: "${pageContext.request.contextPath}/order/review-insert",
            type: "POST",
            data: formData,
            success: function(response) {
                if(response === "success") {
                    alert("리뷰가 성공적으로 등록되었습니다!");
                    $("#reviewModal, #modalBg").hide();
                    $("#reviewForm")[0].reset();
                    location.reload(); // 새로고침하여 리스트 리로드
                } else if(response === "login_required") {
                    alert("로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
                    location.href = "${pageContext.request.contextPath}/login";
                } else {
                    alert("리뷰 등록에 실패했습니다. 다시 시도해 주세요.");
                }
            },
            error: function() {
                alert("서버 통신 에러가 발생했습니다.");
            }
        });
    });
});
</script>