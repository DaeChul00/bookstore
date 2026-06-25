<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container" style="width: 800px; margin: 50px auto; padding: 30px; background: #fff; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.08);">
	<div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 30px;">
		<h2 style="margin: 0; color: #2c3e50;">➕ 새 도서 등록 (관리자 전용)</h2>
		<p style="font-size: 13px; color: #888; margin-top: 5px;">ISBN을 입력하면 외부 API를 통해 도서 정보를 자동으로 불러올 수 있습니다.</p>
	</div>

	<form action="${pageContext.request.contextPath}/book/insert" method="POST" id="insertBookForm">
		<input type="hidden" name="id" value="0">

		<div class="mb-4" style="display: flex; flex-direction: column; gap: 8px;">
			<label style="font-weight: bold; color: #333;">📌 ISBN 번호</label>
			<div style="display: flex; gap: 10px;">
				<input type="text" id="isbnField" name="isbn" class="form-control" style="flex: 1; padding: 10px; border: 1px solid #ccc; border-radius: 6px;" placeholder="예: 9791198758378" required>
				<button type="button" id="btnFetchApi" class="btn" style="background: #e67e22; color: white; padding: 10px 20px; font-weight: bold; border-radius: 6px; border: none; cursor: pointer; transition: 0.2s;">
					🔍 책 정보 자동 가져오기
				</button>
			</div>
		</div>

		<div class="mb-3" style="display: flex; flex-direction: column; gap: 8px;">
			<label style="font-weight: bold; color: #333;">📘 도서 제목</label>
			<input type="text" name="title" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" required>
		</div>

		<div style="display: flex; gap: 20px; class="mb-3">
			<div style="flex: 1; display: flex; flex-direction: column; gap: 8px;">
				<label style="font-weight: bold; color: #333;">✍️ 저자</label>
				<input type="text" name="author" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" required>
			</div>
			<div style="flex: 1; display: flex; flex-direction: column; gap: 8px;">
				<label style="font-weight: bold; color: #333;">🏢 출판사</label>
				<input type="text" name="publisher" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" required>
			</div>
		</div>

		<div style="display: flex; gap: 20px; margin-top: 15px;" class="mb-3">
			<div style="flex: 1; display: flex; flex-direction: column; gap: 8px;">
				<label style="font-weight: bold; color: #333;">📅 출판일</label>
				<input type="text" name="publictiondate" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" placeholder="예: 2026년 03월 01일" required>
			</div>
			<div style="flex: 1; display: flex; flex-direction: column; gap: 8px;">
				<label style="font-weight: bold; color: #333;">💵 정가 (원)</label>
				<input type="number" name="price" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" required>
			</div>
		</div>

		<div class="mb-3" style="display: flex; flex-direction: column; gap: 8px; margin-top: 15px;">
			<label style="font-weight: bold; color: #333;">🖼️ 도서 이미지 커버 URL</label>
			<input type="text" id="imageUrlField" name="bookimage" class="form-control" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px;" required>
			<div id="previewArea" style="margin-top: 10px; display: none;">
				<p style="font-size: 12px; color: #666; margin-bottom: 5px;">미리보기:</p>
				<img id="imgPreview" src="" style="height: 150px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; box-shadow: 2px 2px 8px rgba(0,0,0,0.1);">
			</div>
		</div>

		<div class="mb-4" style="display: flex; flex-direction: column; gap: 8px; margin-top: 15px;">
			<label style="font-weight: bold; color: #333;">📝 도서 소개 / 내용</label>
			<textarea name="content" class="form-control" rows="6" style="padding: 10px; border: 1px solid #ccc; border-radius: 6px; resize: none;" placeholder="도서의 줄거리나 핵심 요약 소개글을 적어주세요." required></textarea>
		</div>

		<input type="hidden" name="rating" value="0.0">

		<div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 30px;">
			<a href="${pageContext.request.contextPath}/book/list" class="btn" style="background: #fafafa; border: 1px solid #ccc; color: #333; padding: 12px 25px; border-radius: 6px; text-decoration: none; font-weight: bold;">취소</a>
			<button type="submit" class="btn" style="background: #2c3e50; color: white; padding: 12px 35px; border-radius: 6px; border: none; font-weight: bold; cursor: pointer;">✨ 신규 도서 등록하기</button>
		</div>
	</form>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
	$("#btnFetchApi").on("click", function() {
		let isbnVal = $("#isbnField").val().trim();
		
		if (isbnVal === "") {
			alert("ISBN 번호를 정확히 입력해 주세요.");
			$("#isbnField").focus();
			return;
		}

		// 로딩 알림 처리
		let $btn = $(this);
		$btn.text("⏳ 조회 중...").prop("disabled", true);

		$.ajax({
			url: "${pageContext.request.contextPath}/book/fetchBookInfo",
			type: "GET",
			data: { isbn: isbnVal },
			dataType: "json",
			success: function(book) {
				if (book) {
					// 백엔드로부터 가공되어 넘어온 BookVO 정보를 각 input 폼에 바인딩
					$("input[name='title']").val(book.title);
					$("input[name='author']").val(book.author);
					$("input[name='publisher']").val(book.publisher);
					$("input[name='publictiondate']").val(book.publictiondate);
					$("input[name='price']").val(book.price);
					$("#imageUrlField").val(book.bookimage);
					$("textarea[name='content']").val(book.content);

					// 이미지 미리보기 세팅
					if (book.bookimage) {
						$("#imgPreview").attr("src", book.bookimage);
						$("#previewArea").show();
					} else {
						$("#previewArea").hide();
					}
					alert("🎉 도서 정보를 성공적으로 호출하여 자동 세팅했습니다!");
				} else {
					alert("❌ 해당 ISBN으로 도서 정보를 조회할 수 없습니다. 번호를 다시 확인해 주세요.");
				}
			},
			error: function() {
				alert("API 조회 중 통신 에러가 발생했습니다.");
			},
			complete: function() {
				// 버튼 원상태 복구
				$btn.text("🔍 책 정보 자동 가져오기").prop("disabled", false);
			}
		});
	});

	// 이미지 URL 주소를 수동으로 수정할 때도 미리보기 동적 반영
	$("#imageUrlField").on("change input", function() {
		let url = $(this).val().trim();
		if (url !== "") {
			$("#imgPreview").attr("src", url);
			$("#previewArea").show();
		} else {
			$("#previewArea").hide();
		}
	});
});
</script>