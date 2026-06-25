<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container" style="padding: 40px 0; width: 1100px; margin: 0 auto;">
	<div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px;">
		<h2 style="margin: 0; color: #2c3e50;">📜 나의 주문 내역</h2>
		<p style="font-size: 13px; color: #888; margin-top: 5px;">최근 주문한 순서대로 표시됩니다.</p>
	</div>

	<table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
		<thead style="background-color: #f8f9fa; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">
			<tr style="height: 50px; text-align: center; font-size: 14px;">
				<th style="width: 15%;">이미지</th>
				<th style="width: 30%;">상품 정보</th>
				<th style="width: 10%;">수량</th>
				<th style="width: 15%;">결제 금액</th>
				<th style="width: 15%;">주문 일자</th>
				<th style="width: 15%;">리뷰 및 배송</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty orderList}">
					<tr style="height: 150px; text-align: center;">
						<td colspan="6" style="color: #999;">주문 내역이 한 건도 존재하지 않습니다.</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="order" items="${orderList}">
						<tr style="height: 110px; text-align: center; border-bottom: 1px solid #eee;">
							<td><img src="${order.bookimage}" style="width: 60px; height: 85px; object-fit: cover; border-radius: 4px; box-shadow: 1px 1px 5px rgba(0,0,0,0.1);"></td>
							<td style="text-align: left; padding-left: 20px;">
								<a href="${pageContext.request.contextPath}/book/view?id=${order.bookId}" style="font-weight: bold; color: #2c3e50; text-decoration: none;">${order.title}</a>
							</td>
							<td><strong>${order.count}개</strong></td>
							<td><span style="color: #e67e22; font-weight: bold;"><fmt:formatNumber value="${order.orderPrice}" type="number"/>원</span></td>
							<td style="font-size: 13px; color: #666;">${order.orderDate}</td>
							<td>
								<button type="button" class="btn btn-sm btn-outline-primary mb-2" onclick="openReviewModal('${order.bookId}', '${order.title}')">✍️ 리뷰 작성</button>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>

<div id="modalBg" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999;"></div>
<div id="reviewModal" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 500px; background: #fff; border-radius: 12px; padding: 30px; box-shadow: 0 5px 25px rgba(0,0,0,0.15); z-index: 1000;">
	<h4 id="modalBookTitle" style="font-weight: bold; color: #2c3e50; margin-bottom: 20px;">✍️ 리뷰 작성</h4>
	<form id="reviewForm">
		<input type="hidden" id="modalBookId" name="bookId">
		<div class="mb-3">
			<label class="form-label" style="font-weight: bold;">⭐ 평점 선택</label>
			<select name="rating" class="form-select">
				<option value="5">★★★★★ (5점 - 최고예요)</option>
				<option value="4">★★★★☆ (4점 - 좋아요)</option>
				<option value="3">★★★☆☆ (3점 - 보통이에요)</option>
				<option value="2">★★☆☆☆ (2점 - 별로예요)</option>
				<option value="1">★☆☆☆☆ (1점 - 최악이에요)</option>
			</select>
		</div>
		<div class="mb-3">
			<label class="form-label" style="font-weight: bold;">내용 작성</label>
			<textarea name="content" class="form-control" rows="4" placeholder="도서에 대한 솔직한 평을 10자 이상 남겨주세요." required></textarea>
		</div>
		<div class="text-end">
			<button type="button" id="closeModalBtn" class="btn btn-light me-2">취소</button>
			<button type="submit" class="btn btn-dark">등록하기</button>
		</div>
	</form>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
function openReviewModal(bookId, bookTitle) {
	$("#modalBookId").val(bookId);
	$("#modalBookTitle").text("✍️ [" + bookTitle + "] 리뷰 작성");
	$("#reviewModal, #modalBg").show();
}

$(document).ready(function() {
	$("#closeModalBtn, #modalBg").on("click", function() {
		$("#reviewModal, #modalBg").hide();
		$("#reviewForm")[0].reset();
	});

	$("#reviewForm").on("submit", function(e) {
		e.preventDefault();
		let content = $("textarea[name='content']").val();
		if (content.trim().length < 10) {
			alert("리뷰 내용을 10자 이상 적어주세요.");
			return;
		}

		$.ajax({
			url : "${pageContext.request.contextPath}/order/review-insert",
			type : "POST",
			data : $(this).serialize(),
			success : function(response) {
				let res = response.trim();
				if (res === "success") {
					alert("리뷰가 성공적으로 등록되었습니다!");
					location.reload();
				} else if (res === "already_exists") {
					alert("❌ 이미 해당 도서에 대한 리뷰를 작성하셨습니다.");
				} else {
					alert("리뷰 등록에 실패했습니다.");
				}
			}
		});
	});
});
</script>